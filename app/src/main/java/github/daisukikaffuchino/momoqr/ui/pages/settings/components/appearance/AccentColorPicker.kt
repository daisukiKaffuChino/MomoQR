package github.daisukikaffuchino.momoqr.ui.pages.settings.components.appearance

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.logic.model.ThemeAccentColor
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.LazyRowSettingsItem
import github.daisukikaffuchino.momoqr.ui.theme.Defaults
import github.daisukikaffuchino.momoqr.ui.theme.shapeByInteraction
import github.daisukikaffuchino.momoqr.utils.VibrationUtil

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ThemeAccentColorPicker(
    colorSelected: ThemeAccentColor,
    onColorSelect: (ThemeAccentColor) -> Unit
) {

    val options = remember { ThemeAccentColor.entries.toList() }

    LazyRowSettingsItem(
        title = stringResource(R.string.pref_preset_scheme),
        description = stringResource(R.string.pref_preset_scheme_desc),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        fadedEdgeWidth = Defaults.fadedEdgeWidth,
        trailingContent = {
            Image(
                painter = painterResource(R.drawable.ic_millennium),
                contentDescription = null,
                colorFilter = ColorFilter.tint(
                    MaterialTheme.colorScheme.primary.copy(
                        alpha = 0.15f
                    )
                ),
                modifier = Modifier.size(48.dp)
            )
        }
    ) {
        items(items = options, key = { it.id }) { option ->
            val isSelected = colorSelected == option
            ColorPaletteCircle(
                colors = option.colors,
                name = option.label,
                selected = isSelected,
                onClick = { onColorSelect(option) }
            )
        }
    }

}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ColorPaletteCircle(
    colors: List<Color>,
    name: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shapes: ButtonShapes = Defaults.shapes()
) {
    require(colors.size >= 4)
    val view = LocalView.current

    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val animatedShape =
        shapeByInteraction(shapes, pressed, Defaults.shapesDefaultAnimationSpec)

    val boxModifier = Modifier
        .size(90.dp)
        .clip(MaterialTheme.shapes.large)
        .background(MaterialTheme.colorScheme.surfaceContainer)
        .then(
            if (selected) {
                Modifier.border(
                    width = 3.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.large
                )
            } else {
                Modifier
            }
        )

    Column(
        modifier = modifier
            .clip(animatedShape)
            .clickable(
                interactionSource = interactionSource,
                role = Role.Button,
                onClick = {
                    VibrationUtil.performHapticFeedback(view)
                    onClick()
                }
            )
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = boxModifier.padding(16.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            ) {
                drawArc(
                    color = colors[1],
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = true
                )
                drawArc(
                    color = colors[2],
                    startAngle = 0f,
                    sweepAngle = 90f,
                    useCenter = true
                )
                drawArc(
                    color = colors[3],
                    startAngle = 90f,
                    sweepAngle = 90f,
                    useCenter = true
                )
            }
        }

        Spacer(Modifier.size(8.dp))

        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}