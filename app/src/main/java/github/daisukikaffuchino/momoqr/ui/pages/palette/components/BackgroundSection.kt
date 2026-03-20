package github.daisukikaffuchino.momoqr.ui.pages.palette.components

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.ui.pages.palette.PaletteUiState
import github.daisukikaffuchino.momoqr.ui.pages.palette.SectionCard
import github.daisukikaffuchino.momoqr.ui.pages.palette.snapToStep
import github.daisukikaffuchino.momoqr.utils.VibrationUtil
import kotlin.math.roundToInt

@Composable
fun BackgroundSection(
    state: PaletteUiState,
    onPickLogo: () -> Unit,
    onPickBackground: () -> Unit,
    onRemoveLogo: () -> Unit,
    onRemoveBackground: () -> Unit,
    onBackgroundAlphaChanged: (Float) -> Unit
) {
    val view = LocalView.current
    var lastVibratedStep by remember {
        mutableIntStateOf(((state.backgroundAlpha - 0.1f) / 0.1f).roundToInt())
    }

    SectionCard(title = stringResource(R.string.label_palette_logo_background)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            PaletteDualActionRow(
                modifier = Modifier.weight(1f),
                primaryLabel = stringResource(
                    if (state.logoBitmap == null) R.string.action_choose_logo else R.string.action_replace_logo
                ),
                secondaryLabel = stringResource(R.string.action_remove_logo),
                secondaryEnabled = state.logoBitmap != null,
                onPrimaryClick = {
                    VibrationUtil.performHapticFeedback(view)
                    onPickLogo()
                },
                onSecondaryClick = {
                    VibrationUtil.performHapticFeedback(view)
                    onRemoveLogo()
                }
            )

            PaletteDualActionRow(
                modifier = Modifier.weight(1f),
                primaryLabel = stringResource(
                    if (state.backgroundBitmap == null) {
                        R.string.action_choose_background
                    } else {
                        R.string.action_replace_background
                    }
                ),
                secondaryLabel = stringResource(R.string.action_remove_background),
                secondaryEnabled = state.backgroundBitmap != null,
                onPrimaryClick = {
                    VibrationUtil.performHapticFeedback(view)
                    onPickBackground()
                },
                onSecondaryClick = {
                    VibrationUtil.performHapticFeedback(view)
                    onRemoveBackground()
                }
            )

        }

        Text(
            text = stringResource(R.string.label_palette_background_alpha, state.backgroundAlpha),
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(top = 16.dp),
        )

        Slider(
            value = state.backgroundAlpha,
            onValueChange = { value ->
                val snapped = snapToStep(value, 0.1f)
                val step = ((snapped - 0.1f) / 0.1f).roundToInt()

                if (step != lastVibratedStep) {
                    VibrationUtil.performHapticFeedback(
                        view,
                        HapticFeedbackConstants.LONG_PRESS
                    )
                    lastVibratedStep = step
                }

                onBackgroundAlphaChanged(snapped)
            },
            valueRange = 0.1f..1.0f,
            steps = 8,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun PaletteDualActionRow(
    modifier: Modifier = Modifier,
    primaryLabel: String,
    secondaryLabel: String,
    onPrimaryClick: () -> Unit,
    onSecondaryClick: () -> Unit,
    secondaryEnabled: Boolean = true,
) {

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy((-6).dp)
    ) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            shapes = ButtonDefaults.shapes(
                (ButtonGroupDefaults.connectedMiddleButtonShapes().shape
                        as RoundedCornerShape)
                    .copy(topStart = CornerSize(100), topEnd = CornerSize(100))
            ),
            onClick = onPrimaryClick
        ) {
            Text(
                text = primaryLabel,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Button(
            modifier = Modifier.fillMaxWidth(),
            shapes = ButtonDefaults.shapes(
                (ButtonGroupDefaults.connectedMiddleButtonShapes().shape
                        as RoundedCornerShape)
                    .copy(bottomStart = CornerSize(100), bottomEnd = CornerSize(100))
            ),
            enabled = secondaryEnabled,
            onClick = onSecondaryClick
        ) {
            Text(
                text = secondaryLabel,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
