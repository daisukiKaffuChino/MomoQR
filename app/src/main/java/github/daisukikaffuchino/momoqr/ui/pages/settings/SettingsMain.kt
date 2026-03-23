package github.daisukikaffuchino.momoqr.ui.pages.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.constants.AppConstants
import github.daisukikaffuchino.momoqr.logic.datastore.DataStoreManager
import github.daisukikaffuchino.momoqr.ui.components.ListItemContainer
import github.daisukikaffuchino.momoqr.ui.components.TopAppBarScaffold
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.SettingsItem
import github.daisukikaffuchino.momoqr.ui.theme.Defaults

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsMain(
    toAppearancePage: () -> Unit,
    toInteractionPage: () -> Unit,
    toCameraPage: () -> Unit,
    toDataPage: () -> Unit,
    toLabPage: () -> Unit,
    toAboutPage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val showLabItem by DataStoreManager.showLabFlow.collectAsState(initial = false)

    TopAppBarScaffold(
        title = stringResource(R.string.page_settings),
        modifier = modifier
    ) {
        ListItemContainer(Modifier.fillMaxSize()) {
            item {
                SettingsItem(
                    leadingIconRes = R.drawable.ic_format_paint,
                    title = stringResource(R.string.pref_appearance),
                    description = stringResource(R.string.pref_appearance_desc),
                    shapes = Defaults.largerShapes(),
                    onClick = toAppearancePage
                )
            }

            item {
                SettingsItem(
                    leadingIconRes = R.drawable.ic_auto_awesome_mosaic,
                    title = stringResource(R.string.pref_interaction),
                    description = stringResource(R.string.pref_interaction_desc),
                    shapes = Defaults.largerShapes(),
                    onClick = toInteractionPage
                )
            }

            item {
                SettingsItem(
                    leadingIconRes = R.drawable.ic_qr_code_scanner,
                    title = stringResource(R.string.pref_camera),
                    description = stringResource(R.string.pref_camera_desc),
                    shapes = Defaults.largerShapes(),
                    onClick = toCameraPage
                )
            }

            item {
                SettingsItem(
                    leadingIconRes = R.drawable.ic_data_table,
                    title = stringResource(R.string.pref_data),
                    description = stringResource(R.string.pref_data_desc),
                    shapes = Defaults.largerShapes(),
                    onClick = toDataPage
                )
            }

            if (showLabItem) {
                item {
                    SettingsItem(
                        leadingIconRes = R.drawable.ic_experiment,
                        title = stringResource(R.string.pref_lab),
                        description = stringResource(R.string.pref_lab_desc),
                        shapes = Defaults.largerShapes(),
                        onClick = toLabPage
                    )
                }
            }

            item {
                SettingsItem(
                    leadingIconRes = R.drawable.ic_info,
                    title = stringResource(R.string.pref_about),
                    description = stringResource(R.string.pref_about_desc),
                    shapes = Defaults.largerShapes(),
                    onClick = toAboutPage
                )
            }
        }
    }
}