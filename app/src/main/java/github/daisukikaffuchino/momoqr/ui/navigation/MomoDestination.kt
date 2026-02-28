package github.daisukikaffuchino.momoqr.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.navigation3.runtime.NavKey
import github.daisukikaffuchino.momoqr.R

enum class MomoDestination(
    val route: NavKey,
    @param:StringRes val label: Int,
    @param:DrawableRes val icon: Int,
    @param:DrawableRes val selectedIcon: Int
) {
    Home(
        route =MomoScreen.Home,
        label = R.string.page_home,
        icon = R.drawable.ic_home,
        selectedIcon = R.drawable.ic_home_filled
    ),
    Stars(
        route = MomoScreen.Stars,
        label = R.string.page_stars,
        icon = R.drawable.ic_star,
        selectedIcon = R.drawable.ic_star_filled
    ),
    Settings(
        route = MomoScreen.Settings.Main,
        label = R.string.page_settings,
        icon = R.drawable.ic_settings,
        selectedIcon = R.drawable.ic_settings_filled
    )
}