package github.daisukikaffuchino.momoqr.ui.pages.result

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SharedTransitionScope
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
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import coil3.compose.AsyncImage
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.constants.Constants
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
import github.daisukikaffuchino.momoqr.ui.pages.result.state.rememberResultState
import github.daisukikaffuchino.momoqr.ui.theme.Defaults
import github.daisukikaffuchino.momoqr.utils.QRCodeGenerateUtil.generateQrBitmap
import androidx.core.net.toUri
import github.daisukikaffuchino.momoqr.ui.components.ConfirmDialog

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
    onNavigateUp = onNavigateUp
)

@Composable
fun SharedTransitionScope.ResultEditPage(
    modifier: Modifier = Modifier,
    stars: StarEntity,
    onSave: (StarEntity) -> Unit,
    onDelete: () -> Unit,
    onNavigateUp: () -> Unit
) = ResultEditorPage(
    stars = stars,
    modifier = modifier.sharedBounds(
        sharedContentState = rememberSharedContentState(key = "${Constants.KEY_STARS_ITEM_TRANSITION}_${stars.id}"),
        animatedVisibilityScope = LocalNavAnimatedContentScope.current,
        resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
    ),
    onSave = onSave,
    onDelete = onDelete,
    onNavigateUp = onNavigateUp
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ResultEditorPage(
    modifier: Modifier = Modifier,
    stars: StarEntity,
    onSave: (StarEntity) -> Unit,
    onDelete: () -> Unit,
    onNavigateUp: () -> Unit
) {
    val scaffoldState = rememberBottomSheetScaffoldState()
    val uiState = rememberResultState(initialData = stars)

    val originalCategories by DataStoreManager.categoriesFlow.collectAsState(initial = emptyList())
    val categories = originalCategories
        .mapIndexed { index, category ->
            ChipItem(
                id = index,
                name = category
            )
        } + ChipItem(id = -2, name = stringResource(R.string.label_unclassified)) +
            ChipItem(id = -1, name = stringResource(R.string.label_customization))

    var defaultIndex by remember { mutableIntStateOf(-2) }
    LaunchedEffect(originalCategories, stars) {
        if (originalCategories.isEmpty()) return@LaunchedEffect
        if (stars.date == 0.toLong()) {
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

    BackHandler(onBack = ::checkModifiedBeforeBack)

    val navBar = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    @SuppressLint("ConfigurationScreenWidthHeight") val screenHeight =
        LocalConfiguration.current.screenHeightDp.dp

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = navBar + 64.dp,
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
                        .offset(y = (-64).dp)
                ) {
                    if (stars.date != 0.toLong()) {
                        ResultFloatingActionButton(
                            text = stringResource(R.string.action_delete),
                            iconRes = R.drawable.ic_delete,
                            expanded = true,
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            onClick = { uiState.showDeleteConfirmDialog = true }
                        )
                    }
                    ResultFloatingActionButton(
                        text = stringResource(R.string.action_starred),
                        iconRes = R.drawable.ic_starred,
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
                item(key = 0){
                    ActionButtonGroup(
                        onSearch = {},
                        onShareText = {},
                        onCopyContent = {},
                        onSaveImage = {},
                        onOpenLink = {},
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
