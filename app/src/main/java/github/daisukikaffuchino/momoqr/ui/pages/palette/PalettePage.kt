package github.daisukikaffuchino.momoqr.ui.pages.palette

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.logic.model.PaletteColorTarget
import github.daisukikaffuchino.momoqr.logic.model.PaletteDotShape
import github.daisukikaffuchino.momoqr.logic.model.PalettePreset
import github.daisukikaffuchino.momoqr.ui.components.ConfirmDialog
import github.daisukikaffuchino.momoqr.ui.components.EmptyListTip
import github.daisukikaffuchino.momoqr.ui.components.EmptyTipType
import github.daisukikaffuchino.momoqr.ui.components.ListItemContainer
import github.daisukikaffuchino.momoqr.ui.components.TopAppBarScaffold
import github.daisukikaffuchino.momoqr.ui.pages.palette.components.BackgroundSection
import github.daisukikaffuchino.momoqr.ui.pages.palette.components.ColorEditor
import github.daisukikaffuchino.momoqr.ui.pages.palette.components.ColorPickerSection
import github.daisukikaffuchino.momoqr.ui.pages.palette.components.DotShapeSection
import github.daisukikaffuchino.momoqr.ui.pages.palette.components.PalettePagerSwitcher
import github.daisukikaffuchino.momoqr.ui.pages.palette.components.PalettePresetPromptDialog
import github.daisukikaffuchino.momoqr.ui.pages.palette.components.PresetItem
import github.daisukikaffuchino.momoqr.ui.pages.palette.components.PreviewSection
import github.daisukikaffuchino.momoqr.ui.pages.result.components.ResultFloatingActionButton
import github.daisukikaffuchino.momoqr.ui.theme.Defaults
import github.daisukikaffuchino.momoqr.utils.VibrationUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class PaletteImageTarget {
    Logo,
    Background,
}

@SuppressLint("LocalContextGetResourceValueCall")
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PalettePage(
    modifier: Modifier = Modifier,
    onNavigateUp: () -> Unit,
    viewModel: PaletteViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val view = LocalView.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var initialized by rememberSaveable { mutableStateOf(false) }
    var currentImageTarget by remember { mutableStateOf<PaletteImageTarget?>(null) }
    var showColorPickerSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSavePresetDialog by remember { mutableStateOf(false) }
    var showRestoreDefaultDialog by remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val pagerState = rememberPagerState(
        initialPage = state.selectedPaneIndex,
        pageCount = { 2 },
    )

    LaunchedEffect(state.selectedPaneIndex) {
        if (pagerState.currentPage != state.selectedPaneIndex) {
            pagerState.animateScrollToPage(state.selectedPaneIndex)
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collectLatest { page ->
            if (page != state.selectedPaneIndex) {
                viewModel.updateSelectedPaneIndex(page)
            }
        }
    }

    LaunchedEffect(initialized) {
        if (!initialized) {
            viewModel.resetEditorState()
            initialized = true
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        val target = currentImageTarget
        currentImageTarget = null
        if (uri == null || target == null) return@rememberLauncherForActivityResult

        scope.launch {
            val bitmap = withContext(Dispatchers.IO) {
                decodeSampledBitmapFromUri(
                    context = context,
                    uri = uri
                )
            }

            if (bitmap == null) {
                snackbarHostState.showSnackbar(
                    context.getString(R.string.toast_palette_image_load_failed)
                )
                return@launch
            }

            when (target) {
                PaletteImageTarget.Logo -> viewModel.setLogoBitmap(bitmap)
                PaletteImageTarget.Background -> viewModel.setBackgroundBitmap(bitmap)
            }
        }
    }

    TopAppBarScaffold(
        title = stringResource(R.string.label_generate_color_palette),
        onBack = onNavigateUp,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            AnimatedVisibility(
                visible = pagerState.currentPage == 0,
                enter = fadeIn() + slideInVertically { it / 2 },
                exit = fadeOut() + slideOutVertically { it / 2 }
            ) {
                Box(
                    modifier = Modifier.padding(8.dp)
                ) {
                    ResultFloatingActionButton(
                        text = stringResource(R.string.action_save),
                        iconRes = R.drawable.ic_save,
                        expanded = true,
                        onClick = {
                            VibrationUtil.performHapticFeedback(view)
                            if (state.previewErrorMessage != null) {
                                Toast.makeText(
                                    context,
                                    R.string.toast_continue_after_eliminate_all_errors,
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@ResultFloatingActionButton
                            } else
                                showSavePresetDialog = true
                        }
                    )
                }
            }
            PalettePresetPromptDialog(
                visible = showSavePresetDialog,
                existingNames = state.presets.mapTo(mutableSetOf()) { it.name },
                presetCount = state.presets.size,
                onSave = { name ->
                    viewModel.savePreset(name)
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            context.getString(R.string.toast_palette_preset_saved)
                        )
                    }
                },
                onDismiss = { showSavePresetDialog = false },
            )
        },
        actions = {
            AnimatedVisibility(
                visible = pagerState.currentPage == 0,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                IconButton(
                    onClick = {
                        VibrationUtil.performHapticFeedback(view)
                        showRestoreDefaultDialog = true
                    }
                ) {
                    Icon(
                        painterResource(R.drawable.ic_restart_alt),
                        contentDescription = stringResource(R.string.action_restore_defaults)
                    )
                }
            }
            ConfirmDialog(
                visible = showRestoreDefaultDialog,
                iconRes = R.drawable.ic_restart_alt,
                title = stringResource(R.string.title_restore_defaults),
                text = stringResource(R.string.tip_restore_palette_defaults),
                confirmButtonText = stringResource(R.string.action_restore_defaults),
                onConfirm = {
                    viewModel.resetEditorState()
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            context.getString(R.string.toast_restored_defaults)
                        )
                    }
                },
                onDismiss = { showRestoreDefaultDialog = false },
            )
        },
        modifier = modifier,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            PalettePagerSwitcher(
                currentPage = pagerState.currentPage,
                onPageSelected = { page ->
                    scope.launch {
                        pagerState.animateScrollToPage(page)
                    }
                },
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
            ) { page ->
                when (page) {
                    0 -> PaletteEditorPage(
                        state = state,
                        isLandscape = isLandscape,
                        onContentChanged = viewModel::updatePreviewContent,
                        onSelectColorTarget = {
                            viewModel.selectColorTarget(it)
                            showColorPickerSheet = true
                        },
                        onPickColorFromBackgroundChanged = viewModel::updatePickColorFromBackground,
                        onDotShapeChanged = viewModel::updateDotShape,
                        onDotScaleChanged = viewModel::updateDotScale,
                        onBackgroundAlphaChanged = viewModel::updateBackgroundAlpha,
                        onBorderWidthChanged = viewModel::updateBorderWidth,
                        onPickLogo = {
                            launchImagePicker(
                                target = PaletteImageTarget.Logo,
                                onTargetSet = { currentImageTarget = it },
                                launcher = imagePickerLauncher::launch,
                            )
                        },
                        onPickBackground = {
                            launchImagePicker(
                                target = PaletteImageTarget.Background,
                                onTargetSet = { currentImageTarget = it },
                                launcher = imagePickerLauncher::launch,
                            )
                        },
                        onRemoveLogo = viewModel::clearLogoBitmap,
                        onRemoveBackground = {
                            viewModel.clearBackgroundBitmap()
                            viewModel.updatePickColorFromBackground(false)
                        }
                    )

                    1 -> PalettePresetListPage(
                        presets = state.presets,
                        onApplyPreset = { preset ->
                            viewModel.applyPreset(preset)
                            scope.launch {
                                pagerState.animateScrollToPage(0)
                                snackbarHostState.showSnackbar(
                                    context.getString(R.string.toast_palette_preset_applied)
                                )
                            }
                        },
                        onDeletePreset = { preset ->
                            viewModel.deletePreset(preset)
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    context.getString(R.string.toast_palette_preset_deleted)
                                )
                            }
                        },
                    )
                }
            }
        }

        if (showColorPickerSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    scope.launch {
                        sheetState.hide()
                        showColorPickerSheet = false
                    }
                },
                sheetState = sheetState,
                dragHandle = null,
                sheetGesturesEnabled = false,
            ) {

                ColorEditor(
                    color = state.editingColor,
                    targetLabel = state.selectedColorTarget.label(),
                    onColorChanged = viewModel::updateSelectedColor,
                    onClose = {
                        scope.launch {
                            sheetState.hide()
                            showColorPickerSheet = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(
                            start = Defaults.screenHorizontalPadding,
                            end = Defaults.screenHorizontalPadding,
                            bottom = 24.dp,
                        ),
                )

            }
        }

    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun PaletteEditorPage(
    state: PaletteUiState,
    isLandscape: Boolean,
    onContentChanged: (String) -> Unit,
    onSelectColorTarget: (PaletteColorTarget) -> Unit,
    onPickColorFromBackgroundChanged: (Boolean) -> Unit,
    onDotShapeChanged: (PaletteDotShape) -> Unit,
    onDotScaleChanged: (Float) -> Unit,
    onBackgroundAlphaChanged: (Float) -> Unit,
    onBorderWidthChanged: (Int) -> Unit,
    onPickLogo: () -> Unit,
    onPickBackground: () -> Unit,
    onRemoveLogo: () -> Unit,
    onRemoveBackground: () -> Unit
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(if (isLandscape) 2 else 1),
        state = state.editorGridState,
        verticalItemSpacing = 16.dp,
        horizontalArrangement = Arrangement.spacedBy(Defaults.settingsItemPadding),
        modifier = Modifier
            .fillMaxSize()
    ) {
        item(key = 0) {
            PreviewSection(
                state = state,
                onContentChanged = onContentChanged,
            )
        }

        item(key = 1) {
            ColorPickerSection(
                state = state,
                onSelectColorTarget = onSelectColorTarget,
                onPickColorFromBackgroundChanged = onPickColorFromBackgroundChanged,
            )
        }

        item(key = 2) {
            DotShapeSection(
                state = state,
                onDotShapeChanged = onDotShapeChanged,
                onDotScaleChanged = onDotScaleChanged,
            )
        }

        item(key = 3) {
            BackgroundSection(
                state = state,
                onPickLogo = onPickLogo,
                onPickBackground = onPickBackground,
                onRemoveLogo = onRemoveLogo,
                onRemoveBackground = onRemoveBackground,
                onBackgroundAlphaChanged = onBackgroundAlphaChanged,
                onBorderWidthChanged = onBorderWidthChanged,
            )
        }

        item(key = 4) {
            Spacer(modifier = Modifier.height(72.dp))
        }
    }
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun PalettePresetListPage(
    presets: List<PalettePreset>,
    onApplyPreset: (PalettePreset) -> Unit,
    onDeletePreset: (PalettePreset) -> Unit,
) {
    val listState = rememberLazyListState()

    AnimatedContent(
        targetState = presets.isEmpty(),
        modifier = Modifier.fillMaxSize(),
    ) { isEmpty ->
        if (isEmpty) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Defaults.screenHorizontalPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                EmptyListTip(
                    type = EmptyTipType.List,
                    size = 96.dp,
                )
                Text(
                    text = stringResource(R.string.tip_palette_no_presets),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        } else {
            ListItemContainer(
                modifier = Modifier
                    .fillMaxSize(),
                state = listState,
            ) {
                items(
                    items = presets,
                    key = { preset -> preset.id },
                ) { preset ->
                    PresetItem(
                        preset = preset,
                        onApplyPreset = onApplyPreset,
                        onDeletePreset = onDeletePreset,
                    )
                }
            }
        }
    }
}

@Composable
fun SectionCard(
    title: String,
    padding: Dp = 16.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(
                horizontal = Defaults.screenVerticalPadding,
                vertical = Defaults.settingsItemPadding
            )
        )
        Spacer(modifier = Modifier.height(Defaults.settingsItemPadding))
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.large)
                .background(Defaults.Colors.Container)
        ) {
            Column(
                modifier = Modifier.padding(padding)
            ) {
                content()
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    PalettePage(
        modifier = Modifier,
        onNavigateUp = {}
    )
}
