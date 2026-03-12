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
import androidx.compose.runtime.mutableIntStateOf
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
import github.daisukikaffuchino.momoqr.logic.model.QRCodeECL
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.MoreContentSettingsItem
import github.daisukikaffuchino.momoqr.utils.VibrationUtil
import kotlin.math.roundToInt

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun ErrorCorrectionPicker(
    modifier: Modifier = Modifier,
    currentLevel: QRCodeECL,
    onLevelChanged: (QRCodeECL) -> Unit,
) {
    val view = LocalView.current
    val context = LocalContext.current
    MoreContentSettingsItem(
        title = stringResource(R.string.pref_error_correction_level),
        description = stringResource(R.string.pref_error_correction_level_desc),
        modifier = modifier
    ) {
        val levels = QRCodeECL.entries
        val levelNames = levels.map { it.nameString }

        var lastVibratedIndex by remember { mutableIntStateOf(currentLevel.ordinal) }

        Slider(
            modifier = Modifier.semantics {
                contentDescription =
                    context.getString(R.string.pref_contrast_level) + levelNames[currentLevel.ordinal]
                stateDescription = levelNames[currentLevel.ordinal]
                liveRegion = LiveRegionMode.Polite
            },

            value = currentLevel.ordinal.toFloat(),

            onValueChange = { newValue ->
                val index = newValue.roundToInt().coerceIn(0, levels.lastIndex)

                if (index != currentLevel.ordinal) {
                    onLevelChanged(levels[index])
                }

                if (index != lastVibratedIndex) {
                    VibrationUtil.performHapticFeedback(
                        view,
                        HapticFeedbackConstants.LONG_PRESS
                    )
                    lastVibratedIndex = index
                }
            },

            valueRange = 0f..levels.lastIndex.toFloat(),
            steps = levels.size - 2
//            onValueChangeFinished = {
//                println("最终选中的值: $lastVibratedIndex")
//            }
        )

        Spacer(Modifier.size(5.dp))

        CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodyMedium) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clearAndSetSemantics {},
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("~7%")
                Text("~15%")
                Text("~25%")
                Text("~30%")
            }
        }
    }
}