package github.daisukikaffuchino.momoqr.ui.pages.stars

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.constants.Constants
import github.daisukikaffuchino.momoqr.logic.database.StarEntity
import github.daisukikaffuchino.momoqr.ui.components.ConfirmDialog
import github.daisukikaffuchino.momoqr.ui.components.EmptyListTip
import github.daisukikaffuchino.momoqr.ui.components.EmptyTipType
import github.daisukikaffuchino.momoqr.ui.components.TopAppBarScaffold
import github.daisukikaffuchino.momoqr.ui.pages.stars.components.StarCard
import github.daisukikaffuchino.momoqr.ui.pages.stars.components.StarSearchTextField
import github.daisukikaffuchino.momoqr.ui.pages.stars.components.StarTopAppBar
import github.daisukikaffuchino.momoqr.ui.theme.Defaults
import github.daisukikaffuchino.momoqr.ui.theme.fadeScale
import github.daisukikaffuchino.momoqr.ui.viewmodels.MainViewModel
import github.daisukikaffuchino.momoqr.utils.toLocalDateString

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SharedTransitionScope.StarsPage(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    toResultEditPage: (StarEntity) -> Unit,
) {
    val starLists by viewModel.sortedStarList.collectAsState(initial = emptyList())
    val selectedStars = viewModel.selectedStarIds.collectAsState()

    // 状态持久化
    val searchFieldState = viewModel.searchFieldState
    var showDeleteConfirmDialog by rememberSaveable { mutableStateOf(false) }

    val selectedStarIds by remember { derivedStateOf { selectedStars.value } }
    val inSelectedMode by remember { derivedStateOf { !selectedStarIds.isEmpty() } }

    val filteredStarList = if (viewModel.searchMode) starLists.filter {
        it.content.contains(searchFieldState.text, ignoreCase = true) ||
                it.category.contains(searchFieldState.text, ignoreCase = true) || it.date.toLocalDateString()
            .contains(searchFieldState.text, ignoreCase = true)
    } else starLists

    val transitionSpec = fadeScale()

    // 当按下返回键（或进行返回操作）时清空选择，仅在非选择模式下生效
    BackHandler(inSelectedMode) { viewModel.clearAllStarsSelection() }

    // 选择时自动退出搜索模式
    LaunchedEffect(inSelectedMode) { if (inSelectedMode) viewModel.setSearchModeEnabled(false) }

    TopAppBarScaffold(
        topBar = {
            StarTopAppBar(
                selectedIds = selectedStarIds,
                selectedMode = inSelectedMode,
                onCancelSelect = { viewModel.clearAllStarsSelection() },
                onSelectAll = { viewModel.selectAllStars() },
                selectedAll = viewModel.isAllSelected(),
                onDeleteSelected = { showDeleteConfirmDialog = true },
                onSearchModeChange = { viewModel.setSearchModeEnabled(it) },
                searchMode = viewModel.searchMode,
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        modifier = modifier
    ) {
        Column {
            AnimatedVisibility(
                visible = viewModel.searchMode,
                enter = fadeIn(MaterialTheme.motionScheme.fastEffectsSpec()) + expandVertically(
                    MaterialTheme.motionScheme.fastSpatialSpec()
                ),
                exit = fadeOut(MaterialTheme.motionScheme.fastEffectsSpec()) + shrinkVertically(
                    MaterialTheme.motionScheme.fastSpatialSpec()
                ),
            ) {
                StarSearchTextField(
                    searchMode = viewModel.searchMode,
                    onSearchModeChange = { viewModel.setSearchModeEnabled(it) },
                    textFieldState = searchFieldState
                )
            }
            AnimatedContent(
                targetState = filteredStarList.isEmpty(),
                transitionSpec = { transitionSpec }
            ) {
                if (it) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        EmptyListTip(
                            type = if (viewModel.searchMode) EmptyTipType.Search else EmptyTipType.List,
                            size = 96.dp
                        )

                        Text(
                            text = stringResource(if (viewModel.searchMode) R.string.tip_search_not_found else R.string.tip_no_item),
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                } else {
                    LazyColumn(
                        state = viewModel.starListState,
                        verticalArrangement = Arrangement.spacedBy(Defaults.settingsItemPadding),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            Spacer(modifier = Modifier.size(Defaults.screenVerticalPadding))
                        }
                        items(
                            items = filteredStarList,
                            key = { task -> task.id }
                        ) { starEntity ->
                            StarCard(
                                content = starEntity.content,
                                category = starEntity.category,
                                marked = starEntity.marked,
                                modDate = starEntity.date,
                                selected = selectedStarIds.contains(starEntity.id),
                                onCardClick = {
                                    if (inSelectedMode) {
                                        viewModel.toggleStarSelection(starEntity)
                                    } else {
                                        toResultEditPage(starEntity)
                                    }
                                },
                                onCardLongClick = { viewModel.toggleStarSelection(starEntity) },
                                modifier = Modifier
                                    .sharedBounds(
                                        sharedContentState = rememberSharedContentState(key = "${Constants.KEY_STARS_ITEM_TRANSITION}_${starEntity.id}"),
                                        animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                                        resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
                                    )
                                    .animateItem(
                                        fadeInSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
                                        placementSpec = MaterialTheme.motionScheme.defaultSpatialSpec(),
                                        fadeOutSpec = MaterialTheme.motionScheme.fastEffectsSpec()
                                    )
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.size(Defaults.screenVerticalPadding))
                        }
                    }
                }
            }
        }
        ConfirmDialog(
            visible = showDeleteConfirmDialog,
            iconRes = R.drawable.ic_delete,
            text = stringResource(R.string.tip_delete_item, selectedStarIds.size),
            onConfirm = { viewModel.deleteSelectedStar() },
            onDismiss = { showDeleteConfirmDialog = false }
        )
    }
}