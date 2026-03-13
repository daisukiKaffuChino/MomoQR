package github.daisukikaffuchino.momoqr.ui.pages.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.logic.datastore.DataStoreManager
import github.daisukikaffuchino.momoqr.ui.components.BasicDialog
import github.daisukikaffuchino.momoqr.ui.components.ListItemContainer
import github.daisukikaffuchino.momoqr.ui.components.TopAppBarScaffold
import github.daisukikaffuchino.momoqr.ui.components.segmentedGroup
import github.daisukikaffuchino.momoqr.ui.components.segmentedSection
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.SettingsItem
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.SwitchSettingsItem
import github.daisukikaffuchino.momoqr.utils.getSystemInfo
import github.daisukikaffuchino.momoqr.utils.restartApp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsLab(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var openSysInfoDialog by remember { mutableStateOf(false) }

    TopAppBarScaffold(
        title = stringResource(R.string.pref_lab),
        onBack = onNavigateUp,
        modifier = modifier,
    ) {
        ListItemContainer(Modifier.fillMaxWidth()) {
            segmentedSection(titleString = "Visibility") {
                segmentedGroup {
                    SwitchSettingsItem(
                        checked = true,
                        leadingIconRes = R.drawable.ic_visibility_off,
                        title = stringResource(R.string.pref_show_entry),
                        description = stringResource(R.string.pref_lab),
                        onCheckedChange = {
                            scope.launch { DataStoreManager.setShowLab(false) }
                            onNavigateUp()
                        }
                    )
                }
            }

            segmentedSection(titleString = "ONLY FOR DEVELOPING") {
                segmentedGroup {
                    SettingsItem(
                        leadingIconRes = R.drawable.ic_settings,
                        title = "Restart",
                        description = "Restart the app",
                        onClick = {
                            context.restartApp()
                        }
                    )
                    SettingsItem(
                        leadingIconRes = R.drawable.ic_settings,
                        title = "Throw an Exception",
                        description = "Crash the app",
                        onClick = {
                            throw Exception("Custom Exception")
                        }
                    )
                    SettingsItem(
                        leadingIconRes = R.drawable.ic_settings,
                        title = "System Info",
                        description = "Show system info",
                        onClick = {
                            openSysInfoDialog = true
                        }
                    )
                }
            }

        }
        BasicDialog(
            visible = openSysInfoDialog,
            title = { Text("System Info") },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    SelectionContainer {
                        Text(text = getSystemInfo(context))
                    }
                }
            },
            confirmButton = {
                FilledTonalButton(
                    onClick = { openSysInfoDialog = false },
                    shapes = ButtonDefaults.shapes()
                ) { Text(stringResource(R.string.action_confirm)) }
            },
            onDismissRequest = { openSysInfoDialog = false }
        )
    }
}