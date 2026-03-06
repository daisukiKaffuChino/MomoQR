package github.daisukikaffuchino.momoqr.ui.pages.settings

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
import github.daisukikaffuchino.momoqr.constants.Constants
import github.daisukikaffuchino.momoqr.logic.datastore.DataStoreManager
import github.daisukikaffuchino.momoqr.logic.model.ContrastLevel
import github.daisukikaffuchino.momoqr.logic.model.DarkMode
import github.daisukikaffuchino.momoqr.logic.model.PaletteStyle
import github.daisukikaffuchino.momoqr.ui.components.TopAppBarScaffold
import github.daisukikaffuchino.momoqr.ui.components.CardListItemContainer
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.SettingsItem
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.SwitchSettingsItem
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.appearance.ContrastPicker
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.appearance.DarkModePicker
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.appearance.LanguageItem
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.appearance.PalettePicker
import github.daisukikaffuchino.momoqr.ui.components.segmentedGroup
import github.daisukikaffuchino.momoqr.ui.components.segmentedSection
import github.daisukikaffuchino.momoqr.utils.setAppLanguage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsAppearance(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dynamicColor by DataStoreManager.dynamicColorFlow.collectAsState(initial = Constants.PREF_DYNAMIC_COLOR_DEFAULT)
    val darkMode by DataStoreManager.darkModeFlow.collectAsState(initial = Constants.PREF_DARK_MODE_DEFAULT)
    val paletteStyle by DataStoreManager.paletteStyleFlow.collectAsState(initial = Constants.PREF_PALETTE_STYLE_DEFAULT)
    val contrastLevel by DataStoreManager.contrastLevelFlow.collectAsState(initial = Constants.PREF_CONTRAST_LEVEL_DEFAULT)

    val uriHandler = LocalUriHandler.current
    val scope = rememberCoroutineScope()

    TopAppBarScaffold(
        title = stringResource(R.string.pref_appearance),
        onBack = onNavigateUp,
        modifier = modifier,
    ) {
        CardListItemContainer(Modifier.fillMaxSize()) {

            segmentedSection(R.string.pref_label_theme) {
                segmentedGroup {
                    SwitchSettingsItem(
                        checked = dynamicColor,
                        leadingIconRes = R.drawable.ic_wand_stars,
                        title = stringResource(R.string.pref_appearance_dynamic_color),
                        description = stringResource(R.string.pref_appearance_dynamic_color_desc),
                        onCheckedChange = { scope.launch { DataStoreManager.setDynamicColor(it) } }
                    )
                    DarkModePicker(
                        currentDarkMode = { DarkMode.fromId(darkMode) },
                        onDarkModeChange = { scope.launch { DataStoreManager.setDarkMode(it.id) } }
                    )
                    PalettePicker(
                        currentPalette = { PaletteStyle.fromId(paletteStyle) },
                        onPaletteChange = { scope.launch { DataStoreManager.setPaletteStyle(it.id) } },
                        isDynamicColor = dynamicColor,
                        isDarkMode = DarkMode.fromId(darkMode),
                        contrastLevel = ContrastLevel.fromFloat(contrastLevel)
                    )
                    ContrastPicker(
                        currentContrast = ContrastLevel.fromFloat(contrastLevel),
                        onContrastChange = { scope.launch { DataStoreManager.setContrastLevel(it.value) } }
                    )
                }
            }

            segmentedSection(R.string.pref_language) {
                segmentedGroup {
                    LanguageItem(DataStoreManager) {
                        scope.launch {
                            val dataStoreManager = DataStoreManager
                            dataStoreManager.setLanguage(it.code)
                            setAppLanguage(it.code)
                        }
                    }
                    SettingsItem(
                        leadingIconRes = R.drawable.ic_translate,
                        title = stringResource(R.string.pref_translate),
                        description = stringResource(R.string.pref_translate_desc),
                        onClick = { uriHandler.openUri(Constants.CROWDIN) }
                    )
                }
            }


        }
    }
}