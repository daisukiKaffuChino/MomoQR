package github.daisukikaffuchino.momoqr.ui.pages.palette

import android.graphics.Bitmap
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.logic.model.QrPaletteColorTarget
import github.daisukikaffuchino.momoqr.logic.model.QrPaletteDotShape
import github.daisukikaffuchino.momoqr.logic.model.QrPalettePreset

val QrPaletteDotShape.stringRes: Int
    get() = when (this) {
        QrPaletteDotShape.Square -> R.string.label_palette_shape_square
        QrPaletteDotShape.Circle -> R.string.label_palette_shape_circle
    }

val QrPaletteColorTarget.stringRes: Int
    get() = when (this) {
        QrPaletteColorTarget.Dark -> R.string.label_palette_dark
        QrPaletteColorTarget.Light -> R.string.label_palette_light
        QrPaletteColorTarget.Background -> R.string.label_palette_background
    }

@Composable
fun QrPaletteDotShape.label(): String = stringResource(stringRes)

@Composable
fun QrPaletteColorTarget.label(): String = stringResource(stringRes)

data class PaletteUiState(
    val previewContent: String = "preview://momoqr.app/",
    val darkColorArgb: Int = Color(0xFF000000).toArgb(),
    val lightColorArgb: Int = Color(0x00FFFFFF).toArgb(),
    val backgroundColorArgb: Int = Color(0x00FFFFFF).toArgb(),
    val pickColorFromBackground: Boolean = false,
    val selectedColorTarget: QrPaletteColorTarget = QrPaletteColorTarget.Dark,
    val dotShape: QrPaletteDotShape = QrPaletteDotShape.Square,
    val dotScale: Float = 1.0f,
    val backgroundAlpha: Float = 1.0f,
    val borderWidth: Int = 20,
    val logoBitmap: Bitmap? = null,
    val backgroundBitmap: Bitmap? = null,
    val presets: List<QrPalettePreset> = emptyList(),
    val selectedPaneIndex: Int = 0,
    val previewBitmap: Bitmap? = null,
    val isGeneratingPreview: Boolean = false,
    val previewErrorMessage: String? = null,
    val editorGridState: LazyStaggeredGridState = LazyStaggeredGridState()
) {
    val darkColor: Color
        get() = Color(darkColorArgb)

    val lightColor: Color
        get() = Color(lightColorArgb)

    val backgroundColor: Color
        get() = Color(backgroundColorArgb)

    val editingColor: Color
        get() = when (selectedColorTarget) {
            QrPaletteColorTarget.Dark -> darkColor
            QrPaletteColorTarget.Light -> lightColor
            QrPaletteColorTarget.Background -> backgroundColor
        }
}
