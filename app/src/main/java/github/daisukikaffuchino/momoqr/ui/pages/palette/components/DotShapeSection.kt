package github.daisukikaffuchino.momoqr.ui.pages.palette.components

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TonalToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.logic.model.PaletteDotShape
import github.daisukikaffuchino.momoqr.ui.pages.palette.PaletteUiState
import github.daisukikaffuchino.momoqr.ui.pages.palette.SectionCard
import github.daisukikaffuchino.momoqr.ui.pages.palette.label
import github.daisukikaffuchino.momoqr.utils.VibrationUtil
import kotlin.math.round
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DotShapeSection(
    state: PaletteUiState,
    onDotShapeChanged: (PaletteDotShape) -> Unit,
    onDotScaleChanged: (Float) -> Unit
) {
    val view = LocalView.current

    var lastVibratedStep by remember {
        mutableIntStateOf(((state.dotScale - 0.1f) / 0.05f).roundToInt())
    }

    SectionCard(title = stringResource(R.string.label_palette_module_style)) {
        val dotShapes = PaletteDotShape.entries
        Row(
            Modifier.padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
        ) {
            dotShapes.forEachIndexed { index, shape ->
                TonalToggleButton(
                    checked = state.dotShape == shape,
                    onCheckedChange = { checked ->
                        if (checked) {
                            VibrationUtil.performHapticFeedback(view)
                            onDotShapeChanged(shape)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shapes = when (index) {
                        0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                        else -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                    },
                ) {
                    Icon(
                        painter = when (index) {
                            0 -> painterResource(R.drawable.ic_square)
                            else -> painterResource(R.drawable.ic_circle)
                        },
                        contentDescription = null,
                    )
                    Spacer(Modifier.size(ToggleButtonDefaults.IconSpacing))
                    Text(shape.label())
                }
            }
        }

        Text(
            text = stringResource(R.string.label_palette_module_scale, state.dotScale),
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(top = 16.dp),
        )

        Slider(
            value = state.dotScale,
            onValueChange = { value ->
                val snapped = (round((value - 0.1f) / 0.05f) * 0.05f + 0.1f)
                    .coerceIn(0.1f, 1.0f)

                val step = ((snapped - 0.1f) / 0.05f).roundToInt()

                if (step != lastVibratedStep) {
                    VibrationUtil.performHapticFeedback(
                        view,
                        HapticFeedbackConstants.LONG_PRESS
                    )
                    lastVibratedStep = step
                }

                onDotScaleChanged(snapped)
            },
            valueRange = 0.1f..1.0f,
            steps = 17,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
