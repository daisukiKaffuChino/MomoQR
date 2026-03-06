package github.daisukikaffuchino.momoqr.ui.pages.scan

import android.util.Log
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.Camera
import androidx.camera.core.CameraInfoUnavailableException
import androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.compose.ui.platform.WindowInfo
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import github.daisukikaffuchino.momoqr.ui.pages.scan.analyzer.CodeDetector
import github.daisukikaffuchino.momoqr.ui.pages.scan.analyzer.ZXingCodeAnalyzer
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class CodeScannerViewModel @Inject constructor() : ViewModel() {

    private var _state = MutableStateFlow(CodeScannerScreenState())
    var state = _state.asStateFlow()

    private val backendEventsChannel = Channel<CodeScannerScreenBackendEvent>()
    val backendEvents = backendEventsChannel.receiveAsFlow()

    private var camera: Camera? = null
    private var imageAnalysis: ImageAnalysis? = null

    fun onEvent(event: CodeScannerScreenUserEvent) {
        when (event) {
            is CodeScannerScreenUserEvent.InitializeCamera -> viewModelScope.launch {
                val imageAnalysisUseCase = getImageAnalysisUseCase()
                val cameraExecutor = Executors.newSingleThreadExecutor()

                imageAnalysisUseCase.setAnalyzer(cameraExecutor, getCodeAnalyzer())

                try {
                    ProcessCameraProvider.configureInstance(Camera2Config.defaultConfig())
                } catch (e: IllegalStateException) {
                    e.fillInStackTrace()
                }

                val processCameraProvider =
                    ProcessCameraProvider.awaitInstance(event.appContext).also { it.unbindAll() }

                try {
                    camera = processCameraProvider.bindToLifecycle(
                        event.lifecycleOwner,
                        DEFAULT_BACK_CAMERA,
                        getCameraPreviewUseCase(),
                        imageAnalysisUseCase
                    ).apply {
                        configureAutoFocus(event.windowInfo)
                    }
                } catch (exception: Exception) {
                    if (exception !is IllegalStateException &&
                        exception !is IllegalArgumentException &&
                        exception !is UnsupportedOperationException &&
                        exception !is CameraInfoUnavailableException
                    ) {
                        throw exception
                    }

                    cleanUpCameraResources(
                        imageAnalysisUseCase,
                        processCameraProvider,
                        cameraExecutor
                    )
                    backendEventsChannel.send(
                        CodeScannerScreenBackendEvent.CameraInitializationError
                    )
                }

                try {
                    awaitCancellation()
                } finally {
                    cleanUpCameraResources(
                        imageAnalysisUseCase,
                        processCameraProvider,
                        cameraExecutor
                    )
                }
            }

            is CodeScannerScreenUserEvent.ToggleFlash -> {
                camera?.let {
                    _state.update { currentState ->
                        val flashEnabled = !currentState.isFlashEnabled
                        it.cameraControl.enableTorch(flashEnabled)
                        currentState.copy(isFlashEnabled = flashEnabled)
                    }
                }
            }

            else -> {
                Log.i("Scanner","NO_ACTION")
            }
        }
    }

    private fun cleanUpCameraResources(
        imageAnalysisUseCase: ImageAnalysis,
        processCameraProvider: ProcessCameraProvider,
        cameraExecutor: ExecutorService?
    ) {
        turnOffFlash()
        imageAnalysisUseCase.clearAnalyzer()
        processCameraProvider.unbindAll()
        camera = null
        cameraExecutor?.let { if (!it.isShutdown) it.shutdownNow() }
    }

    private fun getCodeAnalyzer(): ZXingCodeAnalyzer {
        return ZXingCodeAnalyzer(getBarcodeDetector())
    }

    private fun getBarcodeDetector(): CodeDetector =
        object : CodeDetector {
            override fun onCodeFound(codeValue: String) {
                viewModelScope.launch {
                    //Toast.makeText(MomoApplication.context,codeValue, Toast.LENGTH_SHORT).show()
                    //sendResult(codeValue)
                    imageAnalysis?.clearAnalyzer()
                    backendEventsChannel.send(
                        CodeScannerScreenBackendEvent.CodeScanComplete(codeValue)
                    )
                }
            }

            override fun onError(exception: Exception) {
                Log.e("BarcodeDetector", exception.toString())
            }
        }

    private fun getCameraPreviewUseCase() =
        Preview.Builder().build().apply {
            setSurfaceProvider { newSurfaceRequest ->
                _state.update { currentState ->
                    currentState.copy(surfaceRequest = newSurfaceRequest)
                }
            }
        }

    private fun getImageAnalysisUseCase(): ImageAnalysis {
        imageAnalysis = ImageAnalysis.Builder().apply {
            setResolutionSelector(ResolutionSelector.Builder().build())
            setOutputImageRotationEnabled(true)
        }.build()

        return imageAnalysis!!
    }

    private fun Camera.configureAutoFocus(windowInfo: WindowInfo) {
        val windowHeight = windowInfo.containerSize.height.toFloat()
        val windowWidth = windowInfo.containerSize.width.toFloat()
        val autoFocusPoint = SurfaceOrientedMeteringPointFactory(
            windowWidth,
            windowHeight
        ).createPoint(windowWidth / 2, windowHeight / 2)

        cameraControl.startFocusAndMetering(
            FocusMeteringAction
                .Builder(autoFocusPoint, FocusMeteringAction.FLAG_AF)
                .setAutoCancelDuration(2, TimeUnit.SECONDS)
                .build()
        )
    }

    private fun turnOffFlash() {
        if (state.value.isFlashEnabled) {
            camera?.cameraControl?.enableTorch(false)
        }
    }


}