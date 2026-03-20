package github.daisukikaffuchino.momoqr.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import github.daisukikaffuchino.momoqr.utils.VibrationUtil

@Composable
fun TextCheckbox(
    modifier: Modifier = Modifier,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit,
    stringRes: Int
) {
    val view = LocalView.current
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .toggleable(
                value = checked,
                onValueChange = {
                    if (enabled) {
                        VibrationUtil.performHapticFeedback(
                            view,
                            HapticFeedbackConstants.LONG_PRESS
                        )
                        onCheckedChange(it)
                    }
                },
                role = Role.Checkbox,
                indication = null,
                interactionSource = interactionSource,
            )
    ) {
        Checkbox(
            checked = checked,
            enabled = enabled,
            onCheckedChange = {
                VibrationUtil.performHapticFeedback(view, HapticFeedbackConstants.LONG_PRESS)
                onCheckedChange(it)
            },
            interactionSource = interactionSource
        )
        Text(
            text = stringResource(stringRes),
            style = if (enabled)
                MaterialTheme.typography.labelLarge
            else
                MaterialTheme.typography.labelLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.66f)
                )
        )
    }
}