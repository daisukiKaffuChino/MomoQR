package github.daisukikaffuchino.momoqr.ui.pages.scan

import android.widget.Toast
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.ui.pages.scan.components.CodeScanner
import github.daisukikaffuchino.momoqr.ui.viewmodels.SharedViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ScanPage(
    onNavigateUp: () -> Unit,
) {

    val sharedViewModel = hiltViewModel<SharedViewModel>()
    val codeScannerViewModel = hiltViewModel<CodeScannerViewModel>()
    val codeScannerScreenState by codeScannerViewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var handled by remember { mutableStateOf(false) }

    ObserveAsEvents(
        flow = codeScannerViewModel.backendEvents,
        onEvent = { event ->
            when (event) {
               is CodeScannerScreenBackendEvent.CodeScanComplete -> {
                    if (!handled) {
                        handled = true
                        scope.launch {
                            sharedViewModel.sendScanResult(event.codeValue)
                            delay(300)
                            onNavigateUp()
                        }
                    }
                }
               is CodeScannerScreenBackendEvent.CameraInitializationError -> {
                    Toast.makeText(
                        context,
                        R.string.toast_failed_to_initialize_camera,
                        Toast.LENGTH_LONG
                    ).show()
                    onNavigateUp()
                }
            }
        }
    )

    val lifecycleOwner = LocalLifecycleOwner.current
    val windowInfo = LocalWindowInfo.current

    LaunchedEffect(lifecycleOwner) {
        codeScannerViewModel.onEvent(
            CodeScannerScreenUserEvent.InitializeCamera(
                appContext = context,
                lifecycleOwner = lifecycleOwner,
                windowInfo = windowInfo
            )
        )
    }

    CustomCodeScannerScreenContent(
        state = codeScannerScreenState,
        onEvent = { event ->
            when (event) {
                is CodeScannerScreenUserEvent.CloseClicked -> onNavigateUp()
                else -> codeScannerViewModel.onEvent(event)
            }
        }
    )

}

@Composable
fun CustomCodeScannerScreenContent(
    state: CodeScannerScreenState,
    onEvent: (CodeScannerScreenUserEvent) -> Unit
) {
    Scaffold(containerColor = Color.White) { paddingValues ->
        CodeScanner(
            surfaceRequest = state.surfaceRequest,
            isFlashEnabled = state.isFlashEnabled,
            onCloseClicked = { onEvent(CodeScannerScreenUserEvent.CloseClicked) },
            onToggleFlash = { onEvent(CodeScannerScreenUserEvent.ToggleFlash) },
            paddingValues = paddingValues
        )
    }
}

@Composable
fun <T> ObserveAsEvents(flow: Flow<T>, onEvent: (T) -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(key1 = flow, key2 = lifecycleOwner.lifecycle) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main.immediate) {
                flow.collect(onEvent)
            }
        }
    }
}
