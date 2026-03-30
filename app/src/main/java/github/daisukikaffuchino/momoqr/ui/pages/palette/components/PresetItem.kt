package github.daisukikaffuchino.momoqr.ui.pages.palette.components

import androidx.compose.foundation.basicMarquee
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.logic.model.QrPalettePreset
import github.daisukikaffuchino.momoqr.ui.pages.palette.presetSummary
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.SettingsItem
import github.daisukikaffuchino.momoqr.ui.theme.Defaults

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PresetItem(
    preset: QrPalettePreset,
    onApplyPreset: (QrPalettePreset) -> Unit,
    onDeletePreset: (QrPalettePreset) -> Unit
) {
    SettingsItem(
        shapes = Defaults.largerShapes(),
        headlineContent = {
            Text(
                text = preset.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.basicMarquee(),
            )
        },
        supportingContent = {
            Text(
                text = presetSummary(preset),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        trailingContent = {
            FilledTonalIconButton(
                shapes = IconButtonDefaults.shapes(),
                onClick = { onDeletePreset(preset) },
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_delete),
                    contentDescription = stringResource(R.string.action_delete),
                )
            }
        },
        onClick = { onApplyPreset(preset) },
    )
}
