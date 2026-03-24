package github.daisukikaffuchino.momoqr.ui.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import github.daisukikaffuchino.momoqr.ui.pages.factory.FactoryPage
import github.daisukikaffuchino.momoqr.ui.pages.home.HomePage
import github.daisukikaffuchino.momoqr.ui.pages.palette.PalettePage
import github.daisukikaffuchino.momoqr.ui.pages.result.ResultAddPage
import github.daisukikaffuchino.momoqr.ui.pages.result.ResultEditPage
import github.daisukikaffuchino.momoqr.ui.pages.scan.ScanPage
import github.daisukikaffuchino.momoqr.ui.pages.settings.SettingsAbout
import github.daisukikaffuchino.momoqr.ui.pages.settings.SettingsAboutDonate
import github.daisukikaffuchino.momoqr.ui.pages.settings.SettingsAboutLicence
import github.daisukikaffuchino.momoqr.ui.pages.settings.SettingsAppearance
import github.daisukikaffuchino.momoqr.ui.pages.settings.SettingsCamera
import github.daisukikaffuchino.momoqr.ui.pages.settings.SettingsData
import github.daisukikaffuchino.momoqr.ui.pages.settings.SettingsDataCategory
import github.daisukikaffuchino.momoqr.ui.pages.settings.SettingsInteraction
import github.daisukikaffuchino.momoqr.ui.pages.settings.SettingsLab
import github.daisukikaffuchino.momoqr.ui.pages.settings.SettingsMain
import github.daisukikaffuchino.momoqr.ui.pages.stars.StarsPage
import github.daisukikaffuchino.momoqr.ui.theme.fadeScale
import github.daisukikaffuchino.momoqr.ui.theme.materialSharedAxisX
import github.daisukikaffuchino.momoqr.ui.theme.veilFade
import github.daisukikaffuchino.momoqr.ui.viewmodels.MainViewModel

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TopNavigation(
    backStack: TopLevelBackStack<NavKey>,
    modifier: Modifier = Modifier,
    viewModel: MainViewModel
) {
    fun onBack() {
        backStack.removeLast()
    }

    val veilColor = MaterialTheme.colorScheme.surfaceDim
    fun resultTransition() = NavDisplay.transitionSpec {
        veilFade(veilColor)
    } + NavDisplay.popTransitionSpec {
        veilFade(veilColor)
    } + NavDisplay.predictivePopTransitionSpec {
        veilFade(veilColor)
    }

    val initialOffestFactor = 0.10f
    fun customPageTransition() = NavDisplay.transitionSpec {
        materialSharedAxisX(
            initialOffsetX = { (it * initialOffestFactor).toInt() },
            targetOffsetX = { -(it * initialOffestFactor).toInt() }
        )
    } + NavDisplay.popTransitionSpec {
        materialSharedAxisX(
            initialOffsetX = { -(it * initialOffestFactor).toInt() },
            targetOffsetX = { (it * initialOffestFactor).toInt() }
        )
    } + NavDisplay.predictivePopTransitionSpec {
        materialSharedAxisX(
            initialOffsetX = { -(it * initialOffestFactor).toInt() },
            targetOffsetX = { (it * initialOffestFactor).toInt() }
        )
    }

    val defaultTransition = fadeScale(
        effectSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
        spatialSpec = MaterialTheme.motionScheme.defaultSpatialSpec()
    )

    SharedTransitionLayout {
        NavDisplay(
            backStack = backStack.backStack,
            onBack = ::onBack,
            transitionSpec = { defaultTransition },
            popTransitionSpec = { defaultTransition },
            predictivePopTransitionSpec = { defaultTransition },
            entryProvider = entryProvider {
                entry<MomoScreen.Home> {
                    HomePage(
                        toScanPage = { backStack.add(MomoScreen.Home.Scan) },
                        toPalettePage = { backStack.add(MomoScreen.Home.Palette) },
                        toResultAddPage = { backStack.add(MomoScreen.Result.Add(it)) },
                        toFactoryPage = { backStack.add(MomoScreen.Home.Factory) }
                    )
                }

                entry<MomoScreen.Home.Scan>(metadata = customPageTransition()) {
                    ScanPage(onNavigateUp = ::onBack)
                }

                entry<MomoScreen.Home.Palette>(metadata = customPageTransition()) {
                    PalettePage(onNavigateUp = ::onBack)
                }

                entry<MomoScreen.Home.Factory>(metadata = customPageTransition()) {
                    FactoryPage(
                        onNavigateUp = ::onBack,
                        toResultAddPage = { backStack.add(MomoScreen.Result.Add(it)) })
                }

                entry<MomoScreen.Stars> {
                    StarsPage(
                        viewModel = viewModel,
                        toResultEditPage = { backStack.add(MomoScreen.Result.Edit(it)) }
                    )
                }

                entry<MomoScreen.Result.Add>(metadata = customPageTransition()) { args ->
                    ResultAddPage(
                        stars = args.stars,
                        onSave = {
                            viewModel.addStar(it)
                            onBack()
                        },
                        onNavigateUp = ::onBack
                    )
                }

                entry<MomoScreen.Result.Edit>(metadata = resultTransition()) { args ->
                    ResultEditPage(
                        stars = args.stars,
                        onSave = {
                            viewModel.addStar(it)
                            onBack()
                        },
                        onDelete = {
                            viewModel.deleteStar(args.stars)
                            onBack()
                        },
                        onNavigateUp = ::onBack
                    )
                }

                entry<MomoScreen.Settings.Main> {
                    SettingsMain(
                        toAppearancePage = { backStack.add(MomoScreen.Settings.Appearance) },
                        toAboutPage = { backStack.add(MomoScreen.Settings.About) },
                        toInteractionPage = { backStack.add(MomoScreen.Settings.Interaction) },
                        toCameraPage = { backStack.add(MomoScreen.Settings.Camera) },
                        toDataPage = { backStack.add(MomoScreen.Settings.Data) },
                        toLabPage = { backStack.add(MomoScreen.Settings.Lab) }
                    )
                }

                entry<MomoScreen.Settings.Appearance>(metadata = customPageTransition()) {
                    SettingsAppearance(onNavigateUp = ::onBack)
                }

                entry<MomoScreen.Settings.Interaction>(metadata = customPageTransition()) {
                    SettingsInteraction(onNavigateUp = ::onBack)
                }

                entry<MomoScreen.Settings.Camera>(metadata = customPageTransition()) {
                    SettingsCamera(onNavigateUp = ::onBack)
                }

                entry<MomoScreen.Settings.Data>(metadata = customPageTransition()) {
                    SettingsData(
                        viewModel = viewModel,
                        toCategoryManager = { backStack.add(MomoScreen.Settings.DataCategory) },
                        onNavigateUp = ::onBack
                    )
                }

                entry<MomoScreen.Settings.DataCategory>(metadata = customPageTransition()) {
                    SettingsDataCategory(onNavigateUp = ::onBack)
                }

                entry<MomoScreen.Settings.Lab>(metadata = customPageTransition()) {
                    SettingsLab(onNavigateUp = ::onBack)
                }

                entry<MomoScreen.Settings.About>(metadata = customPageTransition()) {
                    SettingsAbout(
                        toDonatePage = { backStack.add(MomoScreen.Settings.AboutDonate) },
                        toLicencePage = { backStack.add(MomoScreen.Settings.AboutLicence) },
                        onNavigateUp = ::onBack,
                    )
                }

                entry<MomoScreen.Settings.AboutDonate>(metadata = customPageTransition()) {
                    SettingsAboutDonate(onNavigateUp = ::onBack)
                }

                entry<MomoScreen.Settings.AboutLicence>(metadata = customPageTransition()) {
                    SettingsAboutLicence(onNavigateUp = ::onBack)
                }

            },
            modifier = modifier,
        )
    }
}

