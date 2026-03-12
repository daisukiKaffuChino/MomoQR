package github.daisukikaffuchino.momoqr.ui.pages.settings.components.appearance

import android.annotation.SuppressLint
import android.view.HapticFeedbackConstants
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.logic.model.ContrastLevel
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.MoreContentSettingsItem
import github.daisukikaffuchino.momoqr.utils.VibrationUtil

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun ContrastPicker(
    modifier: Modifier = Modifier,
    currentContrast: ContrastLevel,
    onContrastChange: (ContrastLevel) -> Unit,
) {
    val view = LocalView.current
    val context = LocalContext.current
    MoreContentSettingsItem(
        title = stringResource(R.string.pref_contrast_level),
        description = stringResource(R.string.pref_contrast_level_desc),
        modifier = modifier
    ) {
        val contrastLevelName = ContrastLevel.entries.map { stringResource(it.nameRes) }
        var lastVibratedLevel by remember { mutableFloatStateOf(currentContrast.value) }

        Slider(
            modifier = Modifier.semantics {
                contentDescription =
                    context.getString(R.string.pref_contrast_level) + contrastLevelName[currentContrast.ordinal]
                stateDescription = contrastLevelName[currentContrast.ordinal]
                liveRegion = LiveRegionMode.Polite
            },
            value = currentContrast.value,
            onValueChange = { newValue ->
                onContrastChange(ContrastLevel.fromFloat(newValue))
                if (newValue != lastVibratedLevel) {
                    VibrationUtil.performHapticFeedback(
                        view,
                        HapticFeedbackConstants.LONG_PRESS
                    )
                    lastVibratedLevel = newValue
                }
            },
            valueRange = -1f..1f,
            steps = 3,
        )

        Spacer(Modifier.size(5.dp))

        CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodyMedium) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clearAndSetSemantics {},
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(stringResource(R.string.contrast_very_low))
                Text(stringResource(R.string.contrast_low))
                Text(stringResource(R.string.contrast_default))
                Text(stringResource(R.string.contrast_high))
                Text(stringResource(R.string.contrast_very_high))
            }
        }
    }
}