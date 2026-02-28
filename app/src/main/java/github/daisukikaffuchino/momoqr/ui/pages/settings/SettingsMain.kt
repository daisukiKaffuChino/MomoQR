package github.daisukikaffuchino.momoqr.ui.pages.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.ui.components.TopAppBarScaffold
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.SettingsContainer
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.SettingsItem

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsMain(
    toAppearancePage: () -> Unit,
    toInterfacePage: () -> Unit,
    toCameraPage: () -> Unit,
    toDataPage: () -> Unit,
    toAboutPage: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBarScaffold(
        title = stringResource(R.string.page_settings),
        modifier = modifier
    ) {
        SettingsContainer(Modifier.fillMaxSize()) {
            item {
                SettingsItem(
                    leadingIconRes = R.drawable.ic_palette,
                    title = stringResource(R.string.pref_appearance),
                    description = stringResource(R.string.pref_appearance_desc),
                    onClick = toAppearancePage
                )
            }

            item {
                SettingsItem(
                    leadingIconRes = R.drawable.ic_view_comfy,
                    title = stringResource(R.string.pref_interface_interaction),
                    description = stringResource(R.string.pref_interface_interaction_desc),
                    onClick = toInterfacePage
                )
            }

            item {
                SettingsItem(
                    leadingIconRes = R.drawable.ic_qr_code_scanner,
                    title = stringResource(R.string.pref_camera),
                    description = stringResource(R.string.pref_camera_desc),
                    onClick = toCameraPage
                )
            }

            item {
                SettingsItem(
                    leadingIconRes = R.drawable.ic_data_table,
                    title = stringResource(R.string.pref_data),
                    description = stringResource(R.string.pref_data_desc),
                    onClick = toDataPage
                )
            }

            item {
                SettingsItem(
                    leadingIconRes = R.drawable.ic_info,
                    title = stringResource(R.string.pref_about),
                    description = stringResource(R.string.pref_about_desc),
                    onClick = toAboutPage
                )
            }
        }
    }
}