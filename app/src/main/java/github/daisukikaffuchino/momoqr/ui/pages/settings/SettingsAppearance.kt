package github.daisukikaffuchino.momoqr.ui.pages.settings

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.constants.AppConstants
import github.daisukikaffuchino.momoqr.logic.datastore.DataStoreManager
import github.daisukikaffuchino.momoqr.logic.model.ContrastLevel
import github.daisukikaffuchino.momoqr.logic.model.DarkMode
import github.daisukikaffuchino.momoqr.logic.model.PaletteStyle
import github.daisukikaffuchino.momoqr.ui.components.ListItemContainer
import github.daisukikaffuchino.momoqr.ui.components.TopAppBarScaffold
import github.daisukikaffuchino.momoqr.ui.components.segmentedGroup
import github.daisukikaffuchino.momoqr.ui.components.segmentedSection
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.SettingsItem
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.SwitchSettingsItem
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.appearance.AppPalettePicker
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.appearance.ContrastPicker
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.appearance.DarkModePicker
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.appearance.LanguageItem
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.appearance.ThemeAccentColorPicker
import github.daisukikaffuchino.momoqr.utils.setAppLanguage
import github.daisukikaffuchino.momoqr.logic.model.ThemeAccentColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsAppearance(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dynamicColor by DataStoreManager.dynamicColorFlow.collectAsState(initial = AppConstants.PREF_DYNAMIC_COLOR_DEFAULT)
    val accentColor by DataStoreManager.accentColorFlow.collectAsState(initial = ThemeAccentColor.PINK)
    val darkMode by DataStoreManager.darkModeFlow.collectAsState(initial = AppConstants.PREF_DARK_MODE_DEFAULT)
    val paletteStyle by DataStoreManager.paletteStyleFlow.collectAsState(initial = AppConstants.PREF_PALETTE_STYLE_DEFAULT)
    val contrastLevel by DataStoreManager.contrastLevelFlow.collectAsState(initial = AppConstants.PREF_CONTRAST_LEVEL_DEFAULT)
    val homeClassicCard by DataStoreManager.homeClassicCardFlow.collectAsState(initial = AppConstants.PREF_HOME_CLASSIC_CARD_DEFAULT)
    val showHiddenContrastLevel by DataStoreManager.hiddenOptionContrastLevelFlow.collectAsState(
        initial = false
    )

    val uriHandler = LocalUriHandler.current
    val scope = rememberCoroutineScope()

    TopAppBarScaffold(
        title = stringResource(R.string.pref_appearance),
        onBack = onNavigateUp,
        modifier = modifier,
    ) {
        ListItemContainer(Modifier
            .fillMaxSize()
            .animateContentSize()) {
            segmentedSection(R.string.pref_label_accent_color) {
                segmentedGroup(
                    modifier = Modifier.animateContentSize()
                ) {
                    SwitchSettingsItem(
                        checked = dynamicColor,
                        enabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S,
                        leadingIconRes = R.drawable.ic_wand_stars,
                        title = stringResource(R.string.pref_appearance_dynamic_color),
                        description = stringResource(R.string.pref_appearance_dynamic_color_desc),
                        onCheckedChange = { scope.launch { DataStoreManager.setDynamicColor(it) } }
                    )
                    AnimatedVisibility(
                        visible = !dynamicColor,
                        enter = fadeIn() + slideInVertically { it / 2 },
                        exit = fadeOut() + slideOutVertically { it / 2 }
                    ) {
                        ThemeAccentColorPicker(
                            colorSelected = accentColor,
                            onColorSelect = { color ->
                                scope.launch {
                                    DataStoreManager.setAccentColor(color)
                                }
                            }
                        )
                    }
                }
            }

            segmentedSection(R.string.pref_label_display) {
                segmentedGroup {
                    DarkModePicker(
                        currentDarkMode = { DarkMode.fromId(darkMode) },
                        onDarkModeChange = { scope.launch { DataStoreManager.setDarkMode(it.id) } }
                    )
                    AppPalettePicker(
                        currentPalette = { PaletteStyle.fromId(paletteStyle) },
                        onPaletteChange = { scope.launch { DataStoreManager.setPaletteStyle(it.id) } },
                        isDynamicColor = dynamicColor,
                        isDarkMode = DarkMode.fromId(darkMode),
                        contrastLevel = ContrastLevel.fromFloat(contrastLevel)
                    )
                    if (showHiddenContrastLevel) {
                        ContrastPicker(
                            currentContrast = ContrastLevel.fromFloat(contrastLevel),
                            onContrastChange = { scope.launch { DataStoreManager.setContrastLevel(it.value) } }
                        )
                    }
                    SwitchSettingsItem(
                        checked = homeClassicCard,
                        leadingIconRes = R.drawable.ic_view_agenda,
                        title = stringResource(R.string.pref_classic_home_card),
                        description = stringResource(R.string.pref_classic_home_card_desc),
                        onCheckedChange = { scope.launch { DataStoreManager.setHomeClassicCard(it) } }
                    )
                }
            }

            segmentedSection(R.string.pref_language) {
                segmentedGroup {
                    LanguageItem(DataStoreManager) {
                        scope.launch {
                            DataStoreManager.setLanguage(it.code)
                            setAppLanguage(it.code)
                        }
                    }
                    SettingsItem(
                        leadingIconRes = R.drawable.ic_translate,
                        title = stringResource(R.string.pref_translate),
                        description = stringResource(R.string.pref_translate_desc),
                        onClick = { uriHandler.openUri(AppConstants.CROWDIN) }
                    )
                }
            }


        }
    }
}