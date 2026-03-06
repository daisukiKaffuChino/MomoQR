package github.daisukikaffuchino.momoqr.ui.pages.scan

import android.content.Context
import androidx.compose.ui.platform.WindowInfo
import androidx.lifecycle.LifecycleOwner

sealed class CodeScannerScreenUserEvent {
    data object CloseClicked : CodeScannerScreenUserEvent()
    data class InitializeCamera(
        val appContext: Context,
        val lifecycleOwner: LifecycleOwner,
        val windowInfo: WindowInfo
    ) : CodeScannerScreenUserEvent()
    data object ToggleFlash : CodeScannerScreenUserEvent()
}