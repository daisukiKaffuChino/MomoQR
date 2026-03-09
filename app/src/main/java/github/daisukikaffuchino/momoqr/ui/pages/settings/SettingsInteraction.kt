package github.daisukikaffuchino.momoqr.ui.pages.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.constants.Constants
import github.daisukikaffuchino.momoqr.logic.datastore.DataStoreManager
import github.daisukikaffuchino.momoqr.logic.model.SortingMethod
import github.daisukikaffuchino.momoqr.ui.components.CardListItemContainer
import github.daisukikaffuchino.momoqr.ui.components.TopAppBarScaffold
import github.daisukikaffuchino.momoqr.ui.components.segmentedGroup
import github.daisukikaffuchino.momoqr.ui.components.segmentedSection
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.SettingsItem
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.SettingsPlainBox
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.SwitchSettingsItem
import github.daisukikaffuchino.momoqr.ui.theme.Defaults
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsInteraction(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val secureMode by DataStoreManager.secureModeFlow.collectAsState(initial = Constants.PREF_SECURE_MODE_DEFAULT)
    val sortingMethod by DataStoreManager.sortingMethodFlow.collectAsState(initial = Constants.PREF_SORTING_METHOD_DEFAULT)
    val hapticFeedback by DataStoreManager.hapticFeedbackFlow.collectAsState(initial = Constants.PREF_HAPTIC_FEEDBACK_DEFAULT)

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showSortingMethodDialog by rememberSaveable { mutableStateOf(false) }

    TopAppBarScaffold(
        title = stringResource(R.string.pref_interaction),
        onBack = onNavigateUp,
        modifier = modifier,
    ) {
        CardListItemContainer(Modifier.fillMaxSize()) {
            segmentedSection(R.string.pref_label_star_list) {
                segmentedGroup {
                    SettingsItem(
                        leadingIconRes = R.drawable.ic_sort,
                        title = stringResource(R.string.pref_sorting_method),
                        description = stringResource(SortingMethod.fromId(sortingMethod).nameRes),
                        onClick = { showSortingMethodDialog = true }
                    )
                }
            }
            segmentedSection(R.string.pref_label_global) {
                segmentedGroup {
                    SwitchSettingsItem(
                        checked = secureMode,
                        leadingIconRes = R.drawable.ic_shield,
                        title = stringResource(R.string.pref_secure_mode),
                        description = stringResource(R.string.pref_secure_mode_desc),
                        onCheckedChange = { scope.launch { DataStoreManager.setSecureMode(it) } }
                    )
                    SwitchSettingsItem(
                        checked = hapticFeedback,
                        leadingIconRes = R.drawable.ic_touch_long,
                        title = stringResource(R.string.pref_haptic_feedback),
                        description = stringResource(R.string.pref_haptic_feedback_desc),
                        onCheckedChange = { scope.launch { DataStoreManager.setHapticFeedback(it) } }
                    )
                }
            }
            item {
                SettingsPlainBox(stringResource(R.string.tip_haptic_feedback_more_info))
            }
        }

    }

    val sortingList = SortingMethod.entries.map {
        RadioOptions(
            id = it.id,
            text = stringResource(it.nameRes)
        )
    }

    if (showSortingMethodDialog) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                scope.launch {
                    sheetState.hide()
                    showSortingMethodDialog = false
                }
            }
        ) {
            Column(
                Modifier.padding(horizontal = Defaults.settingsItemVerticalPadding)
            ) {
                sortingList.forEach { option ->
                    val selected = option.id == sortingMethod
                    ListItem(
                        onClick = {
                            scope.launch {
                                DataStoreManager.setSortingMethod(option.id)
                                sheetState.hide()
                                showSortingMethodDialog = false
                            }
                        },
                        selected = selected,
                        leadingContent = {
                            RadioButton(selected = selected, onClick = null)
                        },
                        content = { Text(option.text) },
                        colors = ListItemDefaults.colors(containerColor = BottomSheetDefaults.ContainerColor)
                    )
                }
            }
        }
    }

}

data class RadioOptions(
    val id: Int,
    val text: String,
)