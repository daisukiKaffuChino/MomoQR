package github.daisukikaffuchino.momoqr.ui.pages.result.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.logic.model.QrPalettePreset
import github.daisukikaffuchino.momoqr.ui.components.BasicDialog
import github.daisukikaffuchino.momoqr.ui.pages.palette.presetSummary
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.SettingsItem
import github.daisukikaffuchino.momoqr.ui.theme.Defaults
import kotlin.collections.forEach

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ResultPalettePresetDialog(
    visible: Boolean,
    presets: List<QrPalettePreset>,
    selectedPresetId: String?,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    onSelectPreset: (QrPalettePreset) -> Unit,
) {
    BasicDialog(
        visible = visible,
        painter = painterResource(R.drawable.ic_palette),
        title = stringResource(R.string.action_palette_presets),
        text = {
            if (presets.isEmpty()) {
                Text(
                    text = stringResource(R.string.tip_palette_no_presets),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presets.forEach { preset ->
                        SettingsItem(
                            title = preset.name,
                            leadingIcon = {},
                            description = presetSummary(preset),
                            trailingContent = {
                                if (preset.id == selectedPresetId) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_check),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            },
                            shapes = Defaults.largerShapes(),
                            onClick = { onSelectPreset(preset) }
                        )
                    }
                }
            }
        },
        confirmButton = stringResource(R.string.action_restore_defaults),
        dismissButton = stringResource(R.string.action_cancel),
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )
}