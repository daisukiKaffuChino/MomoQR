package github.daisukikaffuchino.momoqr.ui.pages.palette

import android.graphics.Bitmap
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.logic.model.PaletteColorTarget
import github.daisukikaffuchino.momoqr.logic.model.PaletteDotShape
import github.daisukikaffuchino.momoqr.logic.model.PalettePreset

val PaletteDotShape.stringRes: Int
    get() = when (this) {
        PaletteDotShape.Square -> R.string.label_palette_shape_square
        PaletteDotShape.Circle -> R.string.label_palette_shape_circle
    }

val PaletteColorTarget.stringRes: Int
    get() = when (this) {
        PaletteColorTarget.Dark -> R.string.label_palette_dark
        PaletteColorTarget.Light -> R.string.label_palette_light
        PaletteColorTarget.Background -> R.string.label_palette_background
    }

@Composable
fun PaletteDotShape.label(): String = stringResource(stringRes)

@Composable
fun PaletteColorTarget.label(): String = stringResource(stringRes)

data class PaletteUiState(
    val previewContent: String = "preview://momoqr.app/",
    val darkColorArgb: Int = Color(0xFF000000).toArgb(),
    val lightColorArgb: Int = Color(0x00FFFFFF).toArgb(),
    val backgroundColorArgb: Int = Color(0x00FFFFFF).toArgb(),
    val pickColorFromBackground: Boolean = false,
    val selectedColorTarget: PaletteColorTarget = PaletteColorTarget.Dark,
    val dotShape: PaletteDotShape = PaletteDotShape.Square,
    val dotScale: Float = 1.0f,
    val backgroundAlpha: Float = 1.0f,
    val borderWidth: Int = 20,
    val logoBitmap: Bitmap? = null,
    val backgroundBitmap: Bitmap? = null,
    val presets: List<PalettePreset> = emptyList(),
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
            PaletteColorTarget.Dark -> darkColor
            PaletteColorTarget.Light -> lightColor
            PaletteColorTarget.Background -> backgroundColor
        }
}
