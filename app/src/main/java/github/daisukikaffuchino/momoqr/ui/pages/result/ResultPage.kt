package github.daisukikaffuchino.momoqr.ui.pages.result

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.constants.AppConstants
import github.daisukikaffuchino.momoqr.logic.database.StarEntity
import github.daisukikaffuchino.momoqr.logic.datastore.DataStoreManager
import github.daisukikaffuchino.momoqr.ui.components.ChipItem
import github.daisukikaffuchino.momoqr.ui.components.ConfirmDialog
import github.daisukikaffuchino.momoqr.ui.components.TextCheckbox
import github.daisukikaffuchino.momoqr.ui.components.TopAppBarScaffold
import github.daisukikaffuchino.momoqr.ui.pages.result.components.ActionButtonGroup
import github.daisukikaffuchino.momoqr.ui.pages.result.components.ResultCategoryTextField
import github.daisukikaffuchino.momoqr.ui.pages.result.components.ResultContentTextField
import github.daisukikaffuchino.momoqr.ui.pages.result.components.ResultFloatingActionButton
import github.daisukikaffuchino.momoqr.ui.pages.result.components.ResultSheetContent
import github.daisukikaffuchino.momoqr.ui.pages.result.components.StarCategoryChip
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.TertiarySettingsItem
import github.daisukikaffuchino.momoqr.ui.theme.Defaults
import github.daisukikaffuchino.momoqr.utils.LinkOpener
import github.daisukikaffuchino.momoqr.utils.QrAppearanceOptions
import github.daisukikaffuchino.momoqr.utils.QrGenerateUtil.generateQrBitmap
import github.daisukikaffuchino.momoqr.utils.buildSearchUrl
import github.daisukikaffuchino.momoqr.utils.copyToClipboard
import github.daisukikaffuchino.momoqr.utils.keyboardAsState
import github.daisukikaffuchino.momoqr.utils.rememberBitmapSaver
import github.daisukikaffuchino.momoqr.utils.shareText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("ComposableNaming")
@Composable
fun ResultAddPage(
    stars: StarEntity,
    onSave: (StarEntity) -> Unit,
    onNavigateUp: () -> Unit
) = ResultEditorPage(
    stars = stars,
    onSave = onSave,
    onDelete = {},
    onNavigateUp = onNavigateUp,
    skipTransition = true
)

@Composable
fun SharedTransitionScope.ResultEditPage(
    modifier: Modifier = Modifier,
    stars: StarEntity,
    onSave: (StarEntity) -> Unit,
    onDelete: () -> Unit,
    onNavigateUp: () -> Unit
) {
    val animatedScope = LocalNavAnimatedContentScope.current
    ResultEditorPage(
        transitionRunning = animatedScope.transition.isRunning,
        stars = stars,
        modifier = modifier.sharedBounds(
            sharedContentState = rememberSharedContentState(key = "${AppConstants.KEY_STARS_ITEM_TRANSITION}_${stars.id}"),
            animatedVisibilityScope = LocalNavAnimatedContentScope.current,
            resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
        ),
        onSave = onSave,
        onDelete = onDelete,
        onNavigateUp = onNavigateUp
    )
}

@SuppressLint("UseOfNonLambdaOffsetOverload", "LocalContextGetResourceValueCall")
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ResultEditorPage(
    modifier: Modifier = Modifier,
    transitionRunning: Boolean = false,
    skipTransition: Boolean = false,
    stars: StarEntity,
    onSave: (StarEntity) -> Unit,
    onDelete: () -> Unit,
    onNavigateUp: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val uiState = rememberResultState(initialData = stars)
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current

    val isStarEntityEmpty = stars.modifiedDate == 0.toLong()
    val palettePresets by DataStoreManager.palettePresetsFlow.collectAsState(initial = emptyList())
    var showPresetDialog by rememberSaveable { mutableStateOf(false) }
    var selectedPresetId by rememberSaveable { mutableStateOf<String?>(null) }
    var renderSequence by remember { mutableLongStateOf(0L) }
    val selectedPreset by remember(palettePresets, selectedPresetId) {
        derivedStateOf { palettePresets.firstOrNull { it.id == selectedPresetId } }
    }

    val focusManager = LocalFocusManager.current
    val keyboardVisible by keyboardAsState()
    var resultPageTipDismissed by rememberSaveable {
        mutableStateOf(AppConstants.PREF_RESULT_PAGE_TIP_DISMISSED_DEFAULT)
    }

    LaunchedEffect(keyboardVisible) {
        if (!keyboardVisible)
            focusManager.clearFocus()
    }

    LaunchedEffect(Unit) {
        val autoCopy = DataStoreManager.autoCopyFlow.first()
        if (autoCopy && skipTransition) context.copyToClipboard(stars.content)
        resultPageTipDismissed = DataStoreManager.resultPageTipDismissedFlow.first()
    }

    val openInAppBrowser by DataStoreManager.openInAppBrowserFlow.collectAsState(initial = AppConstants.PREF_OPEN_IN_APP_BROWSER_DEFAULT)

    val saveDirectly by DataStoreManager.saveDirectlyFlow.collectAsState(initial = AppConstants.PREF_NOT_ASK_SAVE_PATH_DEFAULT)
    val saveBitmap = context.rememberBitmapSaver(
        notAskForSavePath = saveDirectly,
        onSaveSuccess = { result ->
            Toast.makeText(
                context,
                "${context.getString(R.string.toast_saved)} $result",
                Toast.LENGTH_LONG
            ).show()
        },
        onSaveFailed = {
            Toast.makeText(context, R.string.toast_save_failed, Toast.LENGTH_SHORT).show()
        }
    )

    val originalCategories by DataStoreManager.categoriesFlow.collectAsState(initial = emptyList())
    val unclassified = stringResource(R.string.label_unclassified)
    val customization = stringResource(R.string.label_customization)

    val categories = remember(originalCategories, unclassified, customization) {
        buildList {
            add(ChipItem(-2, unclassified))
            originalCategories.forEachIndexed { index, category ->
                add(ChipItem(index, category))
            }
            add(ChipItem(-1, customization))
        }
    }

    /**
     * stars.category 为空 → -2
    stars.category 和 originalCategories 里某一项同名 → 选中那个预设项的 index
    否则 → -1
     **/

    LaunchedEffect(originalCategories, stars.category) {
        val categoryText = stars.category.trim()
        uiState.selectedCategoryIndex = when {
            categoryText.isEmpty() -> -2
            else -> originalCategories.indexOfFirst { it.trim() == categoryText }
                .takeIf { it >= 0 } ?: -1
        }
    }

    val isCustomCategory by remember { derivedStateOf { uiState.selectedCategoryIndex == -1 } }
    val isUnclassifiedCategory by remember { derivedStateOf { uiState.selectedCategoryIndex == -2 } }

    var qrBitmap by remember(stars.content) { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(uiState.qrContent, uiState.ecl, selectedPreset) {
        val qrRenderQuality = DataStoreManager.qrRenderQualityFlow.first()
        val requestId = renderSequence + 1
        renderSequence = requestId
        val appearance = selectedPreset?.let {
            withContext(Dispatchers.IO) { loadAppearanceFromPreset(context, it) }
        } ?: QrAppearanceOptions()
        generateQrBitmap(
            content = uiState.qrContent,
            eclFloat = uiState.ecl,
            qrSize = qrRenderQuality.getSize(),
            appearance = appearance,
            onSuccess = { bitmap ->
                recycleAppearanceBitmaps(appearance)
                if (requestId != renderSequence) {
                    if (!bitmap.isRecycled) bitmap.recycle()
                    return@generateQrBitmap
                }
                qrBitmap = bitmap
            },
            onError = {
                recycleAppearanceBitmaps(appearance)
                if (requestId != renderSequence) return@generateQrBitmap
                it.printStackTrace()
                qrBitmap = null
            }
        )
    }

    fun checkModifiedBeforeBack() {
        if (uiState.isModified()) {
            uiState.showExitConfirmDialog = true
        } else {
            onNavigateUp()
        }
    }

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded
        )
    )
    val sheetState = scaffoldState.bottomSheetState

    // 转场期间禁用返回，以防闪屏
    val blockBackPress = transitionRunning && !skipTransition

    BackHandler {
        if (blockBackPress) return@BackHandler
        when {
            scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded -> scope.launch {
                scaffoldState.bottomSheetState.partialExpand()
            }

            else -> checkModifiedBeforeBack()
        }
    }

    // 处理动画
    var startSheetAnimation by rememberSaveable { mutableStateOf(false) }
    var lastTransitionRunning by remember { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(transitionRunning, skipTransition) {
        if (startSheetAnimation) return@LaunchedEffect

        if (skipTransition) {
            delay(200)
            startSheetAnimation = true
        } else {
            if (lastTransitionRunning == true && !transitionRunning)
                startSheetAnimation = true
        }

        lastTransitionRunning = transitionRunning
    }

    val navBar = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val animatedSheetPeekHeight by animateDpAsState(
        targetValue = if (startSheetAnimation) navBar + 52.dp else 0.dp,
        label = "sheet_peek_height"
    )
    val animatedFabPeekHeight by animateDpAsState(
        targetValue = if (startSheetAnimation) 52.dp else 0.dp,
        label = "fab_peek_height"
    )

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = animatedSheetPeekHeight,
        sheetDragHandle = {
            val topPadding by animateDpAsState(
                targetValue = if (sheetState.targetValue == SheetValue.Expanded) {
                    WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
                } else 0.dp,
                label = "sheet_drag_handle"
            )

            Box(Modifier.padding(top = topPadding)) {
                BottomSheetDefaults.DragHandle()
            }
        },
        sheetContent = {
            ResultSheetContent(
                qrBitmap = qrBitmap,
                isLandscape = isLandscape,
                selectedPreset = selectedPreset,
                showPresetDialog = showPresetDialog,
                palettePresets = palettePresets,
                selectedPresetId = selectedPresetId,
                stars = stars,
                onShowPresetDialogChange = { showPresetDialog = it },
                onSelectedPresetIdChange = { selectedPresetId = it }
            )
        }
    ) {
        TopAppBarScaffold(
            title = stringResource(R.string.action_edit_result),
            floatingActionButton = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .imePadding()
                        .offset(y = -animatedFabPeekHeight)
                ) {
                    if (!isStarEntityEmpty) {
                        ResultFloatingActionButton(
                            text = stringResource(R.string.action_delete),
                            iconRes = R.drawable.ic_delete,
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            onClick = { uiState.showDeleteConfirmDialog = true }
                        )
                    }
                    ResultFloatingActionButton(
                        text = if (isStarEntityEmpty) stringResource(R.string.action_starred) else
                            stringResource(R.string.action_save),
                        iconRes = if (isStarEntityEmpty) R.drawable.ic_starred else R.drawable.ic_save,
                        onClick = {
                            if (uiState.setErrorIfNotValid()) {
                                return@ResultFloatingActionButton
                            } else {
                                uiState.clearError()

                                val categoryText: String = if (isCustomCategory)
                                    uiState.categoryContent
                                else if (isUnclassifiedCategory)
                                    ""
                                else
                                    categories.firstOrNull { it.id == uiState.selectedCategoryIndex }?.name.orEmpty()

                                val now = System.currentTimeMillis()

                                val item = StarEntity(
                                    id = stars.id,
                                    content = uiState.qrContent,
                                    category = categoryText,
                                    createdDate = if (isStarEntityEmpty) now else stars.createdDate,
                                    modifiedDate = now,
                                    marked = uiState.isMarked
                                )
                                onSave(item)
                            }
                        }
                    )
                }
            },
            onBack = ::checkModifiedBeforeBack,
            modifier = modifier
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = Defaults.screenVerticalPadding)
            ) {
                item(key = 0) {
                    if (!resultPageTipDismissed) {
                        TertiarySettingsItem(
                            modifier = Modifier.padding(bottom = 16.dp),
                            leadingIconRes = R.drawable.ic_auto_awesome,
                            description = stringResource(R.string.tip_result_page_sheet_detail),
                            onClick = {
                                resultPageTipDismissed = true
                                scope.launch { DataStoreManager.setResultPageTipDismissed(true) }
                            }
                        )
                    }
                }

                item(key = 1) {
                    ActionButtonGroup(
                        onSearch = {
                            scope.launch {
                                val searchEngine = DataStoreManager.searchEngineFlow.first()
                                val url = buildSearchUrl(uiState.qrContent, searchEngine)
                                LinkOpener.open(
                                    context = context,
                                    uriHandler = uriHandler,
                                    url = url,
                                    useCustomTabs = openInAppBrowser
                                )
                            }
                        },
                        onOpenLink = {
                            LinkOpener.open(
                                context = context,
                                uriHandler = uriHandler,
                                url = uiState.qrContent,
                                useCustomTabs = openInAppBrowser
                            )
                        },
                        onShareText = { context.shareText(uiState.qrContent) },
                        onCopyContent = { context.copyToClipboard(uiState.qrContent) },
                        onSaveImage = {
                            qrBitmap?.let { saveBitmap(it) }
                        },
                        isUrl = LinkOpener.isValidUrl(uiState.qrContent)
                    )
                }

                item(key = 2) {
                    ResultContentTextField(
                        value = uiState.qrContent,
                        onValueChange = { uiState.qrContent = it },
                        isError = uiState.isErrorContent
                    )
                }

                item(key = 3) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .clip(MaterialTheme.shapes.largeIncreased)
                            .background(Defaults.Colors.Container)
                            .padding(horizontal = 16.dp)
                            .padding(vertical = 12.dp)
                    ) {
                        StarCategoryChip(
                            items = categories,
                            selectedItemIndex = uiState.selectedCategoryIndex,
                            isLoading = originalCategories.isEmpty(),
                            onCategorySelected = { uiState.selectedCategoryIndex = it },
                            modifier = Modifier.fillMaxWidth()
                        )
                        AnimatedVisibility(
                            visible = isCustomCategory,
                            enter = fadeIn(MaterialTheme.motionScheme.defaultEffectsSpec()) + expandVertically(
                                MaterialTheme.motionScheme.defaultEffectsSpec()
                            ),
                            exit = fadeOut(MaterialTheme.motionScheme.defaultEffectsSpec()) + shrinkVertically(
                                MaterialTheme.motionScheme.defaultEffectsSpec()
                            )
                        ) {
                            ResultCategoryTextField(
                                value = uiState.categoryContent,
                                onValueChange = { uiState.categoryContent = it },
                                isError = uiState.isErrorCategory,
                                supportingText = stringResource(uiState.categorySupportingText)
                            )
                        }
                    }
                }

                item(key = 4) {
                    Spacer(modifier = Modifier.size(4.dp))
                    TextCheckbox(
                        checked = uiState.isMarked,
                        onCheckedChange = { uiState.isMarked = it },
                        stringRes = R.string.tip_checkbox_marked,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item(key = 5) {
                    Spacer(modifier = Modifier.height(128.dp))
                }
            }
        }

    }

    ConfirmDialog(
        visible = uiState.showExitConfirmDialog,
        iconRes = R.drawable.ic_undo,
        text = stringResource(R.string.tip_discard_changes),
        onConfirm = {
            uiState.showExitConfirmDialog = false
            onNavigateUp()
        },
        onDismiss = { uiState.showExitConfirmDialog = false }
    )

    ConfirmDialog(
        visible = uiState.showDeleteConfirmDialog,
        iconRes = R.drawable.ic_delete,
        text = stringResource(R.string.tip_delete_item, 1),
        onConfirm = onDelete,
        onDismiss = { uiState.showDeleteConfirmDialog = false }
    )
}

