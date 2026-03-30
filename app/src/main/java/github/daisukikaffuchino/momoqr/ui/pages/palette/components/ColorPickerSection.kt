package github.daisukikaffuchino.momoqr.ui.pages.palette.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.logic.model.QrPaletteColorTarget
import github.daisukikaffuchino.momoqr.ui.components.TextCheckbox
import github.daisukikaffuchino.momoqr.ui.pages.palette.PaletteUiState
import github.daisukikaffuchino.momoqr.ui.pages.palette.SectionCard
import github.daisukikaffuchino.momoqr.ui.pages.palette.label
import github.daisukikaffuchino.momoqr.utils.VibrationUtil
import github.daisukikaffuchino.momoqr.utils.toHexString

@Composable
fun ColorPickerSection(
    state: PaletteUiState,
    onSelectColorTarget: (QrPaletteColorTarget) -> Unit,
    onPickColorFromBackgroundChanged: (Boolean) -> Unit,
) {
    SectionCard(
        title = androidx.compose.ui.res.stringResource(R.string.label_palette_colors),
        padding = 8.dp
    ) {
        ColorSettingRow(
            title = QrPaletteColorTarget.Dark.label(),
            color = state.darkColor,
            onSelect = { onSelectColorTarget(QrPaletteColorTarget.Dark) },
        )

        ColorSettingRow(
            title = QrPaletteColorTarget.Light.label(),
            color = state.lightColor,
            onSelect = { onSelectColorTarget(QrPaletteColorTarget.Light) },
        )

        ColorSettingRow(
            title = QrPaletteColorTarget.Background.label(),
            color = state.backgroundColor,
            onSelect = { onSelectColorTarget(QrPaletteColorTarget.Background) },
        )

        TextCheckbox(
            checked = state.pickColorFromBackground,
            enabled = state.backgroundBitmap != null,
            onCheckedChange = onPickColorFromBackgroundChanged,
            stringRes = R.string.tip_extract_color_from_background_image,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ColorSettingRow(
    title: String,
    color: Color,
    onSelect: () -> Unit,
) {
    val view = LocalView.current
    Surface(
        shape = MaterialTheme.shapes.medium,
        onClick = {
            VibrationUtil.performHapticFeedback(view)
            onSelect()
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(color, shape = MaterialTheme.shapes.small),
                )
                Text(
                    text = color.toHexString(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}
