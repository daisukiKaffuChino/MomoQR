package github.daisukikaffuchino.momoqr.ui.pages.scan

sealed class CodeScannerScreenBackendEvent {
    data class CodeScanComplete(val codeValue: String) : CodeScannerScreenBackendEvent()
    data object CameraInitializationError : CodeScannerScreenBackendEvent()
}