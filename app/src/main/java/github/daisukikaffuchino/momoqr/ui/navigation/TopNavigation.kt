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
import github.daisukikaffuchino.momoqr.ui.pages.home.HomePage
import github.daisukikaffuchino.momoqr.ui.pages.settings.SettingsAbout
import github.daisukikaffuchino.momoqr.ui.pages.settings.SettingsAboutLicence
import github.daisukikaffuchino.momoqr.ui.pages.settings.SettingsAppearance
import github.daisukikaffuchino.momoqr.ui.pages.settings.SettingsData
import github.daisukikaffuchino.momoqr.ui.pages.settings.SettingsDataCategory
import github.daisukikaffuchino.momoqr.ui.pages.settings.SettingsInterface
import github.daisukikaffuchino.momoqr.ui.pages.settings.SettingsMain
import github.daisukikaffuchino.momoqr.ui.pages.stars.StarsPage
import github.daisukikaffuchino.momoqr.ui.theme.fadeScale
import github.daisukikaffuchino.momoqr.ui.theme.materialSharedAxisX
import github.daisukikaffuchino.momoqr.ui.theme.veilFade
import github.daisukikaffuchino.momoqr.ui.viewmodels.MainViewModel
import kotlin.collections.removeLast

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
    fun editorTransition() = NavDisplay.transitionSpec {
        veilFade(veilColor)
    } + NavDisplay.popTransitionSpec {
        veilFade(veilColor)
    } + NavDisplay.predictivePopTransitionSpec {
        veilFade(veilColor)
    }

    val initialOffestFactor = 0.10f
    fun settingsTransition() = NavDisplay.transitionSpec {
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
                    HomePage()
                }

                entry<MomoScreen.Stars> {
                    StarsPage(
                        viewModel = viewModel,
                        resultPage = { backStack.add(MomoScreen.Result.EditResult(it)) }
                    )
                }

//                entry<VerveDoScreen.Editor.Add>(metadata = editorTransition()) {
//                    TaskAddPage(
//                        onSave = {
//                            viewModel.addTodo(it)
//                            onBack()
//                        },
//                        onNavigateUp = ::onBack
//                    )
//                }

//                entry<VerveDoScreen.Editor.Edit>(metadata = editorTransition()) { editorArgs ->
//                    TaskEditPage(
//                        toDo = editorArgs.toDo,
//                        onSave = {
//                            viewModel.addTodo(it)
//                            // 如果原来的待办状态为未完成并且修改后状态为完成
//                            if (!editorArgs.toDo.isCompleted && it.isCompleted) {
//                                viewModel.playConfetti()
//                            }
//                            onBack()
//                        },
//                        onDelete = {
//                            viewModel.deleteTodo(editorArgs.toDo)
//                            onBack()
//                        },
//                        onNavigateUp = ::onBack
//                    )
//                }

                entry<MomoScreen.Settings.Main> {
                    SettingsMain(
                        toAppearancePage = { backStack.add(MomoScreen.Settings.Appearance) },
                        toAboutPage = { backStack.add(MomoScreen.Settings.About) },
                        toInterfacePage = { backStack.add(MomoScreen.Settings.Interface) },
                        toCameraPage = { backStack.add(MomoScreen.Settings.Camera) },
                        toDataPage = { backStack.add(MomoScreen.Settings.Data) },
                    )
                }

                entry<MomoScreen.Settings.Appearance>(metadata = settingsTransition()) {
                    SettingsAppearance(onNavigateUp = ::onBack)
                }

                entry<MomoScreen.Settings.Interface>(metadata = settingsTransition()) {
                    SettingsInterface(onNavigateUp = ::onBack)
                }

                entry<MomoScreen.Settings.Camera>(metadata = settingsTransition()) {
                    SettingsInterface(onNavigateUp = ::onBack)
                }

                entry<MomoScreen.Settings.Data>(metadata = settingsTransition()) {
                    SettingsData(
                        viewModel = viewModel,
                        toCategoryManager = { backStack.add(MomoScreen.Settings.DataCategory) },
                        onNavigateUp = ::onBack
                    )
                }

                entry<MomoScreen.Settings.DataCategory>(metadata = settingsTransition()) {
                    SettingsDataCategory(onNavigateUp = ::onBack)
                }

                entry<MomoScreen.Settings.About>(metadata = settingsTransition()) {
                    SettingsAbout(
                        //toSpecialPage = { backStack.add(TodoScreen.Settings.AboutSpecial) },
                        toLicencePage = { backStack.add(MomoScreen.Settings.AboutLicence) },
                        onNavigateUp = ::onBack,
                    )
                }

                /*entry<TodoScreen.Settings.AboutSpecial>(metadata = settingsTransition()) {
                    SettingsAboutSpecial(viewModel = viewModel)
                }*/

                entry<MomoScreen.Settings.AboutLicence>(metadata = settingsTransition()) {
                    SettingsAboutLicence(onNavigateUp = ::onBack)
                }

            },
            modifier = modifier,
        )
    }
}