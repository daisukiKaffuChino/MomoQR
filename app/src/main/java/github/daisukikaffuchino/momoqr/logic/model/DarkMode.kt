package github.daisukikaffuchino.momoqr.logic.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import github.daisukikaffuchino.momoqr.R

enum class DarkMode(
    val id: Int,
    @param:DrawableRes val iconRes: Int,
    @param:StringRes val nameRes: Int
) {
    FollowSystem(id = -1, iconRes = R.drawable.ic_lightbulb_2, nameRes = R.string.dark_mode_system),
    Light(id = 1, iconRes = R.drawable.ic_light_mode, nameRes = R.string.dark_mode_light),
    Dark(id = 2, iconRes = R.drawable.ic_dark_mode, nameRes = R.string.dark_mode_dark);

    companion object {
        fun fromId(id: Int) = entries.find { it.id == id } ?: FollowSystem
    }
}