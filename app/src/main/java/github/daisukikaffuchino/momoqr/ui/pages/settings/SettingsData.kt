package github.daisukikaffuchino.momoqr.ui.pages.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.constants.AppConstants
import github.daisukikaffuchino.momoqr.logic.datastore.DataStoreManager
import github.daisukikaffuchino.momoqr.ui.components.ListItemContainer
import github.daisukikaffuchino.momoqr.ui.components.TopAppBarScaffold
import github.daisukikaffuchino.momoqr.ui.components.segmentedGroup
import github.daisukikaffuchino.momoqr.ui.components.segmentedSection
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.SettingsItem
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.SwitchSettingsItem
import github.daisukikaffuchino.momoqr.ui.viewmodels.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsData(
    viewModel: MainViewModel,
    toCategoryManager: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val saveDirectly by DataStoreManager.saveDirectlyFlow.collectAsState(initial = AppConstants.PREF_NOT_ASK_SAVE_PATH_DEFAULT)

    val scope = rememberCoroutineScope()

    TopAppBarScaffold(
        title = stringResource(R.string.pref_data),
        onBack = onNavigateUp,
        modifier = modifier,
    ) {
        ListItemContainer(Modifier.fillMaxWidth()) {
            segmentedSection(R.string.pref_label_storage) {
                segmentedGroup {
                    SwitchSettingsItem(
                        checked = saveDirectly,
                        leadingIconRes = R.drawable.ic_add_photo_alternate,
                        title = stringResource(R.string.pref_save_image),
                        description = stringResource(R.string.pref_save_image_desc),
                        onCheckedChange = { scope.launch { DataStoreManager.setSaveDirectly(it) } }
                    )
                }
            }
            segmentedSection(R.string.label_category) {
                segmentedGroup {
                    SettingsItem(
                        leadingIconRes = R.drawable.ic_interests,
                        title = stringResource(R.string.pref_category_management),
                        description = stringResource(R.string.pref_category_management_desc),
                        onClick = toCategoryManager
                    )
                }
            }
        }
    }
}