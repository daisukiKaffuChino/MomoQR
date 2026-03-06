package github.daisukikaffuchino.momoqr.ui.pages.scan

import androidx.camera.core.SurfaceRequest

data class CodeScannerScreenState(
    val surfaceRequest: SurfaceRequest? = null,
    val isFlashEnabled: Boolean = false
)