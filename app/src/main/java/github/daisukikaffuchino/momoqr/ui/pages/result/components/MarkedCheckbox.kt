package github.daisukikaffuchino.momoqr.ui.pages.result.components

import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.utils.VibrationUtil
import android.view.HapticFeedbackConstants
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
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

@Composable
fun MarkedCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .toggleable(
                value = checked,
                onValueChange = {
                    VibrationUtil.performHapticFeedback(view, HapticFeedbackConstants.LONG_PRESS)
                    onCheckedChange(it)
                },
                role = Role.Checkbox,
                indication = null,
                interactionSource = interactionSource,
            )
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = {
                VibrationUtil.performHapticFeedback(view, HapticFeedbackConstants.LONG_PRESS)
                onCheckedChange(it)
            },
            interactionSource = interactionSource
        )
        Text(
            text = stringResource(R.string.tip_checkbox_marked),
            style = MaterialTheme.typography.labelLarge
        )
    }
}