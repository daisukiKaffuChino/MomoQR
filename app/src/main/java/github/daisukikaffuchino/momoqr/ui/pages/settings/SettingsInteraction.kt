package github.daisukikaffuchino.momoqr.ui.pages.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
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
import github.daisukikaffuchino.momoqr.constants.AppConstants
import github.daisukikaffuchino.momoqr.logic.datastore.DataStoreManager
import github.daisukikaffuchino.momoqr.logic.model.SearchEngine
import github.daisukikaffuchino.momoqr.logic.model.SortingMethod
import github.daisukikaffuchino.momoqr.ui.components.ListItemContainer
import github.daisukikaffuchino.momoqr.ui.components.SingleChoiceBottomSheet
import github.daisukikaffuchino.momoqr.ui.components.TopAppBarScaffold
import github.daisukikaffuchino.momoqr.ui.components.segmentedGroup
import github.daisukikaffuchino.momoqr.ui.components.segmentedSection
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.SettingsItem
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.SettingsPlainBox
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.SwitchSettingsItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsInteraction(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val exitConfirmation by DataStoreManager.exitConfirmationFlow.collectAsState(initial = AppConstants.PREF_EXIT_CONFIRMATION_DEFAULT)
    val openInAppBrowser by DataStoreManager.openInAppBrowserFlow.collectAsState(initial = AppConstants.PREF_OPEN_IN_APP_BROWSER_DEFAULT)
    val sortingMethod by DataStoreManager.sortingMethodFlow.collectAsState(initial = AppConstants.PREF_SORTING_METHOD_DEFAULT)
    val starListRelativeTime by DataStoreManager.starListRelativeTimeFlow.collectAsState(initial = AppConstants.PREF_STAR_LIST_RELATIVE_TIME_DEFAULT)
    val hapticFeedback by DataStoreManager.hapticFeedbackFlow.collectAsState(initial = AppConstants.PREF_HAPTIC_FEEDBACK_DEFAULT)
    val searchEngine by DataStoreManager.searchEngineFlow.collectAsState(initial = SearchEngine.GOOGLE)

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showSortingMethodDialog by rememberSaveable { mutableStateOf(false) }
    var showSearchEngineDialog by rememberSaveable { mutableStateOf(false) }

    TopAppBarScaffold(
        title = stringResource(R.string.pref_interaction),
        onBack = onNavigateUp,
        modifier = modifier,
    ) {
        ListItemContainer(Modifier.fillMaxSize()) {
            segmentedSection(R.string.pref_label_star_list) {
                segmentedGroup {
                    SettingsItem(
                        leadingIconRes = R.drawable.ic_sort,
                        title = stringResource(R.string.pref_sorting_method),
                        description = stringResource(SortingMethod.fromId(sortingMethod).nameRes),
                        onClick = { showSortingMethodDialog = true }
                    )
                    SwitchSettingsItem(
                        checked = starListRelativeTime,
                        leadingIconRes = R.drawable.ic_search_activity,
                        title = stringResource(R.string.pref_star_list_relative_time),
                        description = stringResource(R.string.pref_star_list_relative_time_desc),
                        onCheckedChange = { scope.launch { DataStoreManager.setStarListRelativeTime(it) } }
                    )
                }
            }
            segmentedSection(R.string.pref_label_action) {
                segmentedGroup {
                    SettingsItem(
                        leadingIconRes = R.drawable.ic_travel_explore,
                        title = stringResource(R.string.pref_search_engine),
                        description = searchEngine.label,
                        onClick = { showSearchEngineDialog = true }
                    )
                }
            }
            segmentedSection(R.string.pref_label_global) {
                segmentedGroup {
                    SwitchSettingsItem(
                        checked = openInAppBrowser,
                        leadingIconRes = R.drawable.ic_open_in_browser,
                        title = stringResource(R.string.pref_use_in_app_browser),
                        description = stringResource(R.string.pref_use_in_app_browser_desc),
                        onCheckedChange = { scope.launch { DataStoreManager.setOpenInAppBrowser(it) } }
                    )
                    SwitchSettingsItem(
                        checked = exitConfirmation,
                        leadingIconRes = R.drawable.ic_exit_to_app,
                        title = stringResource(R.string.pref_exit_confirmation),
                        description = stringResource(R.string.pref_exit_confirmation_desc),
                        onCheckedChange = { scope.launch { DataStoreManager.setExitConfirmation(it) } }
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

        val sortingList = SortingMethod.entries.map {
            RadioOptions(
                id = it.id,
                text = stringResource(it.nameRes)
            )
        }
        SingleChoiceBottomSheet(
            visible = showSortingMethodDialog,
            sheetState = sheetState,
            options = sortingList,
            selectedOption = sortingList.first { it.id == sortingMethod },
            onDismiss = {
                sheetState.hide()
                showSortingMethodDialog = false
            },
            onOptionClick = { option ->
                DataStoreManager.setSortingMethod(option.id)
                sheetState.hide()
                showSortingMethodDialog = false
            },
            optionText = { option ->
                Text(option.text)
            }
        )
        SingleChoiceBottomSheet(
            visible = showSearchEngineDialog,
            sheetState = sheetState,
            options = SearchEngine.entries,
            selectedOption = searchEngine,
            onDismiss = {
                sheetState.hide()
                showSearchEngineDialog = false
            },
            onOptionClick = { option ->
                DataStoreManager.setSearchEngine(option)
                sheetState.hide()
                showSearchEngineDialog = false
            },
            optionText = { option ->
                Text(option.label)
            }
        )

    }
}

data class RadioOptions(
    val id: Int,
    val text: String,
)
