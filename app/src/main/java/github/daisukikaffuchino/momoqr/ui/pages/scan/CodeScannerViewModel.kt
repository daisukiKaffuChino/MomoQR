package github.daisukikaffuchino.momoqr.ui.pages.scan

import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.Camera
import androidx.camera.core.CameraInfoUnavailableException
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
import androidx.camera.core.CameraSelector.DEFAULT_FRONT_CAMERA
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
import com.google.zxing.BarcodeFormat
import dagger.hilt.android.lifecycle.HiltViewModel
import github.daisukikaffuchino.momoqr.constants.Constants
import github.daisukikaffuchino.momoqr.logic.datastore.DataStoreManager
import github.daisukikaffuchino.momoqr.ui.pages.scan.analyzer.CodeAnalyzer
import github.daisukikaffuchino.momoqr.ui.pages.scan.analyzer.CodeDetector
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
    private var formats: List<BarcodeFormat> = listOf(BarcodeFormat.QR_CODE)
    private var switchCamera = Constants.PREF_SWITCH_CAMERA_DEFAULT
    private var beepSound = Constants.PREF_BEEP_SOUND_DEFAULT
    private var enhancedProcessing = Constants.PREF_ENHANCED_PREPROCESSING_DEFAULT
    private var _state = MutableStateFlow(CodeScannerScreenState())
    var state = _state.asStateFlow()

    private val backendEventsChannel = Channel<CodeScannerScreenBackendEvent>()
    val backendEvents = backendEventsChannel.receiveAsFlow()

    private var camera: Camera? = null
    private var imageAnalysis: ImageAnalysis? = null

    init {
        viewModelScope.launch {
            DataStoreManager.barcodeFormatsFlow.collect { formats = it.toList() }
        }
        viewModelScope.launch {
            DataStoreManager.switchCameraFlow.collect { switchCamera = it }
        }
        viewModelScope.launch {
            DataStoreManager.beepSoundFlow.collect { beepSound = it }
        }
        viewModelScope.launch {
            DataStoreManager.enhancedPreprocessFlow.collect { enhancedProcessing = it }
        }
    }

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

                val selector = safeCameraSelector(processCameraProvider)

                try {
                    camera = processCameraProvider.bindToLifecycle(
                        event.lifecycleOwner,
                        selector,
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

            else -> {}
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

    private fun getCodeAnalyzer(): CodeAnalyzer {
        return CodeAnalyzer(
            codeDetector = getBarcodeDetector(),
            formats = formats,
            enhancedProcessing
        )
    }

    private val toneGenerator = ToneGenerator(
        AudioManager.STREAM_MUSIC,
        100
    )

    private fun playBeep() {
        toneGenerator.startTone(
            ToneGenerator.TONE_PROP_BEEP,
            150
        )
    }

    private fun getBarcodeDetector(): CodeDetector =
        object : CodeDetector {
            override fun onDetected(codeValue: String) {
                viewModelScope.launch {
                    imageAnalysis?.clearAnalyzer()

                    if (beepSound) playBeep()

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

    private fun safeCameraSelector(
        provider: ProcessCameraProvider
    ): CameraSelector {
        return if (switchCamera) {
            if (provider.hasCamera(DEFAULT_FRONT_CAMERA))
                DEFAULT_FRONT_CAMERA
            else
                DEFAULT_BACK_CAMERA
        } else
            DEFAULT_BACK_CAMERA
    }

    override fun onCleared() {
        super.onCleared()
        toneGenerator.release()
    }

}