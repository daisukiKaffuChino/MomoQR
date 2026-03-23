package github.daisukikaffuchino.momoqr.logic.model

import androidx.compose.ui.graphics.Color

enum class ThemeAccentColor(
    val id: Int,
    val colors: List<Color>
) {
    PINK(
        id = 0,
        colors = listOf(
            Color(0xFFF596AA),
            Color(0xFFFFB1BF),
            Color(0xFFE46988),
            Color(0xFFEDBE92)
        )
    ),
    GREEN(
        id = 1,
        colors = listOf(
            Color(0xFF8BC34A),
            Color(0xFF9FD75C),
            Color(0xFF6A9F2B),
            Color(0xFF9FD0CC)
        )
    ),
    YELLOW(
        id = 2,
        colors = listOf(
            Color(0xFFFFF59D),
            Color(0xFFD7CA2C),
            Color(0xFF9E9401),
            Color(0xFFA6D0BA)
        )
    ),
    BLUE(
        id = 3,
        colors = listOf(
            Color(0xFF03A9F4),
            Color(0xFF8ECDFF),
            Color(0xFF0099DD),
            Color(0xFFCFC0E7)
        )
    );

    companion object {
        fun fromId(id: Int): ThemeAccentColor {
            return entries.find { it.id == id } ?: PINK
        }
    }
}