package github.daisukikaffuchino.momoqr.ui.pages.settings.components.appearance

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.logic.model.ThemeAccentColor
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.LazyRowSettingsItem
import github.daisukikaffuchino.momoqr.ui.theme.Defaults
import github.daisukikaffuchino.momoqr.utils.VibrationUtil

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ThemeAccentColorPicker(
    colorSelected: ThemeAccentColor,
    onColorSelect: (ThemeAccentColor) -> Unit
) {
//    Row(
//        horizontalArrangement = Arrangement.spacedBy(16.dp)
//    ) {
//        ThemeAccentColor.entries.forEach { option ->
//            ColorPaletteCircle(
//                colors = option.colors,
//                selected = option == colorSelected,
//                onClick = { onColorSelect(option) }
//            )
//        }
//    }

    val options = remember { ThemeAccentColor.entries.toList() }

    LazyRowSettingsItem(
        title = stringResource(R.string.pref_preset_scheme),
        description = stringResource(R.string.pref_preset_scheme_desc),
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
                selected = isSelected,
                onClick = { onColorSelect(option) }
            )
        }
    }

}

@Composable
fun ColorPaletteCircle(
    colors: List<Color>,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    require(colors.size >= 3)
    val view = LocalView.current

    val scale by animateFloatAsState(
        targetValue = if (selected) 1.15f else 1f,
        label = "palette_scale"
    )

    val borderColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        Color.Transparent
    }

    Column(
        modifier = modifier.padding(vertical = 16.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = modifier
                .size(56.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .shadow(
                    elevation = if (selected) 6.dp else 2.dp,
                    shape = CircleShape
                )
                .clip(CircleShape)
                .clickable(onClick = {
                    VibrationUtil.performHapticFeedback(view)
                    onClick()
                })
                .border(
                    width = if (selected) 3.dp else 1.dp,
                    color = borderColor,
                    shape = CircleShape
                )
        ) {
            Canvas(
                modifier = Modifier.fillMaxSize()
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
    }
}