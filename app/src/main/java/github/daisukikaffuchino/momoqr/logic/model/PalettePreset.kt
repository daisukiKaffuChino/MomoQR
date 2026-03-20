package github.daisukikaffuchino.momoqr.logic.model

import kotlinx.serialization.Serializable

@Serializable
enum class PaletteDotShape {
    Square,
    Circle,
}

@Serializable
enum class PaletteColorTarget {
    Dark,
    Light,
    Background,
}

@Serializable
data class PalettePreset(
    val id: String,
    val name: String,
    val previewContent: String,
    val darkColorArgb: Int,
    val lightColorArgb: Int,
    val backgroundColorArgb: Int,
    val pickColorFromBackground: Boolean,
    val selectedColorTarget: PaletteColorTarget,
    val dotShape: PaletteDotShape,
    val dotScale: Float,
    val backgroundAlpha: Float,
    val borderWidth: Int = 20,
    val logoFileName: String? = null,
    val backgroundFileName: String? = null,
    val createdAt: Long,
    val updatedAt: Long,
)
