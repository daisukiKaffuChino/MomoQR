package github.daisukikaffuchino.momoqr.ui.pages.palette.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.utils.VibrationUtil
import github.daisukikaffuchino.momoqr.utils.toHexString
import io.mhssn.colorpicker.ColorPicker
import io.mhssn.colorpicker.ColorPickerType

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ColorEditor(
    color: Color,
    targetLabel: String,
    onColorChanged: (Color) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val view = LocalView.current

    var pickedColor by remember { mutableStateOf(color) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                Text(
                    text = stringResource(R.string.label_palette_editing, targetLabel),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )
                Text(
                    text = pickedColor.toHexString(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            FilledIconButton(
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                ),
                shapes = IconButtonDefaults.shapes(),
                onClick = {
                    VibrationUtil.performHapticFeedback(view)
                    onColorChanged(pickedColor)
                    onClose()
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_save),
                    contentDescription = stringResource(R.string.action_save)
                )
            }
        }

        ColorPicker(
            modifier = Modifier
                .wrapContentWidth(Alignment.CenterHorizontally),
            type = ColorPickerType.Circle(),
            onPickedColor = { newColor ->
                pickedColor = newColor
            },
        )

    }
}
