package github.daisukikaffuchino.momoqr.ui.pages.result

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import coil3.compose.AsyncImage
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.constants.AppConstants
import github.daisukikaffuchino.momoqr.logic.database.StarEntity
import github.daisukikaffuchino.momoqr.logic.datastore.DataStoreManager
import github.daisukikaffuchino.momoqr.logic.model.QRCodeECL
import github.daisukikaffuchino.momoqr.ui.components.ChipItem
import github.daisukikaffuchino.momoqr.ui.components.TopAppBarScaffold
import github.daisukikaffuchino.momoqr.ui.pages.result.components.ActionButtonGroup
import github.daisukikaffuchino.momoqr.ui.pages.result.components.MarkedCheckbox
import github.daisukikaffuchino.momoqr.ui.pages.result.components.QrPropertyCard
import github.daisukikaffuchino.momoqr.ui.pages.result.components.ResultCategoryTextField
import github.daisukikaffuchino.momoqr.ui.pages.result.components.ResultContentTextField
import github.daisukikaffuchino.momoqr.ui.pages.result.components.ResultFloatingActionButton
import github.daisukikaffuchino.momoqr.ui.pages.result.components.StarCategoryChip
import github.daisukikaffuchino.momoqr.ui.theme.Defaults
import github.daisukikaffuchino.momoqr.utils.QrGenerateUtil.generateQrBitmap
import androidx.core.net.toUri
import github.daisukikaffuchino.momoqr.ui.components.ConfirmDialog
import github.daisukikaffuchino.momoqr.utils.buildSearchUrl
import github.daisukikaffuchino.momoqr.utils.copyToClipboard
import github.daisukikaffuchino.momoqr.utils.rememberBitmapSaver
import github.daisukikaffuchino.momoqr.utils.shareText
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

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

    val isStarEntityEmpty = stars.date == 0.toLong()

    LaunchedEffect(Unit) {
        val autoCopy = DataStoreManager.autoCopyFlow.first()
        if (autoCopy && skipTransition) context.copyToClipboard(stars.content)
    }

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

    val categories = buildList {
        add(ChipItem(-2, unclassified))
        originalCategories.forEachIndexed { index, category ->
            add(ChipItem(index, category))
        }
        add(ChipItem(-1, customization))
    }

    var defaultIndex by remember { mutableIntStateOf(-2) }
    LaunchedEffect(originalCategories, stars) {
        if (originalCategories.isEmpty()) return@LaunchedEffect
        if (isStarEntityEmpty) {
            val index = if (categories.size == 2) -2 else 0
            defaultIndex = index
            uiState.selectedCategoryIndex = index
        } else {
            val index = categories.firstOrNull { it.name == stars.category }?.id ?: -2
            defaultIndex = index
            uiState.selectedCategoryIndex = index
        }
    }

    val isCustomCategory by remember { derivedStateOf { uiState.selectedCategoryIndex == -1 } }
    val isUnclassifiedCategory by remember { derivedStateOf { uiState.selectedCategoryIndex == -2 } }

    var qrBitmap by remember(stars.content) { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(stars.content) {
        generateQrBitmap(
            content = stars.content,
            eclFloat = uiState.ecl,
            onSuccess = { bitmap ->
                qrBitmap = bitmap
            },
            onError = {
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
    @SuppressLint("ConfigurationScreenWidthHeight") val screenHeight =
        LocalConfiguration.current.screenHeightDp.dp
    val animatedSheetPeekHeight by animateDpAsState(
        targetValue = if (startSheetAnimation) navBar + 64.dp else 0.dp,
        label = "sheet_peek_height"
    )
    val animatedFabPeekHeight by animateDpAsState(
        targetValue = if (startSheetAnimation) 64.dp else 0.dp,
        label = "fab_peek_height"
    )

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = animatedSheetPeekHeight,
        sheetContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight * 0.81f)
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = Defaults.screenHorizontalPadding)
                ) {
                    item {
                        qrBitmap?.let {
                            OutlinedCard(
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    AsyncImage(
                                        model = it,
                                        contentDescription = stringResource(R.string.label_image_preview),
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                    }
                    item {
                        QrPropertyCard(
                            modifiedTime = stars.date,
                            errorCorrectionLevel = QRCodeECL.fromFloat(stars.errorCorrectionLevel)
                                .toDisplayString()
                        )
                    }
                }
            }
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
                            expanded = true,
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            onClick = { uiState.showDeleteConfirmDialog = true }
                        )
                    }
                    ResultFloatingActionButton(
                        text = if (isStarEntityEmpty) stringResource(R.string.action_starred) else
                            stringResource(R.string.action_save),
                        iconRes = if (isStarEntityEmpty) R.drawable.ic_starred else R.drawable.ic_save,
                        expanded = true,
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
                                    categories[uiState.selectedCategoryIndex].name

                                val item = StarEntity(
                                    id = stars.id,
                                    content = uiState.qrContent,
                                    category = categoryText,
                                    date = System.currentTimeMillis(),
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
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item(key = 0) {
                    ActionButtonGroup(
                        onSearch = {
                            scope.launch {
                                val searchEngine = DataStoreManager.searchEngineFlow.first()
                                val url = buildSearchUrl(uiState.qrContent, searchEngine)
                                uriHandler.openUri(url)
                            }
                        },
                        onOpenLink = { uriHandler.openUri(uiState.qrContent) },
                        onShareText = { context.shareText(uiState.qrContent) },
                        onCopyContent = { context.copyToClipboard(uiState.qrContent) },
                        onSaveImage = {
                            qrBitmap?.let { saveBitmap(it) }
                        },
                        isUrl = isValidUrl(uiState.qrContent)
                    )
                }

                item(key = 1) {
                    ResultContentTextField(
                        value = uiState.qrContent,
                        onValueChange = { uiState.qrContent = it },
                        isError = uiState.isErrorContent,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item(key = 2) {
                    Text(
                        text = stringResource(R.string.label_category),
                        style = MaterialTheme.typography.titleMedium
                    )
                    StarCategoryChip(
                        items = categories,
                        defaultSelectedItemIndex = defaultIndex,
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
                            supportingText = stringResource(uiState.categorySupportingText),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                item(key = 3) {
                    Spacer(modifier = Modifier.size(4.dp))
                    MarkedCheckbox(
                        checked = uiState.isMarked,
                        onCheckedChange = { uiState.isMarked = it },
                        modifier = Modifier.fillMaxWidth()
                    )
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

private fun isValidUrl(text: String): Boolean {
    return try {
        val uri = text.toUri()
        uri.scheme == "http" || uri.scheme == "https"
    } catch (_: Exception) {
        false
    }
}
