package github.daisukikaffuchino.momoqr.ui.pages.result.components

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.logic.database.StarEntity
import github.daisukikaffuchino.momoqr.logic.model.QrPalettePreset
import github.daisukikaffuchino.momoqr.logic.model.QrCodeECL
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.TertiarySettingsItem
import github.daisukikaffuchino.momoqr.ui.theme.Defaults

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ResultSheetContent(
    qrBitmap: Bitmap?,
    isLandscape: Boolean,
    selectedPreset: QrPalettePreset?,
    showPresetDialog: Boolean,
    palettePresets: List<QrPalettePreset>,
    selectedPresetId: String?,
    stars: StarEntity,
    onShowPresetDialogChange: (Boolean) -> Unit,
    onSelectedPresetIdChange: (String?) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(if (isLandscape) 2 else 1),
            verticalItemSpacing = 16.dp,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
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
                        AsyncImage(
                            model = it,
                            contentDescription = stringResource(R.string.label_image_preview),
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f),
                            contentScale = ContentScale.Crop
                        )
                    }
                } ?: run {
                    Text(
                        text = stringResource(R.string.tip_palette_generate_preview_failed),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    )
                }
            }
            item {
                qrBitmap?.let {
                    TertiarySettingsItem(
                        leadingIconRes = R.drawable.ic_palette,
                        title = stringResource(R.string.action_palette_presets),
                        description = selectedPreset?.name
                            ?: stringResource(R.string.tip_result_palette_preset_desc),
                        onClick = { onShowPresetDialogChange(true) }
                    )
                }
            }
            item {
                QrPropertyCard(
                    createdTime = stars.createdDate,
                    modifiedTime = stars.modifiedDate,
                    errorCorrectionLevel = QrCodeECL.fromFloat(stars.errorCorrectionLevel)
                        .toDisplayString()
                )
            }
        }
        ResultPalettePresetDialog(
            visible = showPresetDialog,
            presets = palettePresets,
            selectedPresetId = selectedPresetId,
            onConfirm = {
                onSelectedPresetIdChange(null)
                onShowPresetDialogChange(false)
            },
            onDismiss = { onShowPresetDialogChange(false) },
            onSelectPreset = { preset ->
                onSelectedPresetIdChange(preset.id)
                onShowPresetDialogChange(false)
            }
        )
    }
}
