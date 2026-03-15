package github.daisukikaffuchino.momoqr.ui.pages.settings.components.appearance

import android.os.Build
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import github.daisukikaffuchino.momoqr.logic.model.ContrastLevel
import github.daisukikaffuchino.momoqr.logic.model.PaletteStyle
import github.daisukikaffuchino.momoqr.ui.theme.Defaults
import github.daisukikaffuchino.momoqr.ui.theme.dynamicColorScheme
import github.daisukikaffuchino.momoqr.ui.theme.shapeByInteraction
import github.daisukikaffuchino.momoqr.utils.VibrationUtil

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppPaletteItem(
    modifier: Modifier = Modifier,
    isDynamicColor: Boolean,
    isDark: Boolean,
    paletteStyle: PaletteStyle,
    contrastLevel: ContrastLevel,
    selected: Boolean,
    onSelect: () -> Unit,
    shapes: ButtonShapes = Defaults.shapes()
) {
    val view = LocalView.current
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val animatedShape =
        shapeByInteraction(shapes, pressed, Defaults.shapesDefaultAnimationSpec)

    Column(
        modifier = modifier
            .width(90.dp)
            .clip(animatedShape)
            .clickable(
                interactionSource = interactionSource,
                role = Role.Button,
                onClick = {
                    VibrationUtil.performHapticFeedback(view)
                    onSelect()
                }
            )
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // 为不同主题样式设置不同色板
        MaterialTheme(
            colorScheme = dynamicColorScheme(
                keyColor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && isDynamicColor) {
                    colorResource(id = android.R.color.system_accent1_500)
                } else {
                    Color(0xFFF596AA)
                },
                isDark = isDark,
                contrastLevel = contrastLevel.value.toDouble(),
                style = paletteStyle
            )
        ) {
            val borderWidth by animateDpAsState(if (selected) 3.dp else (-1).dp)
            // 颜色预览区域
            Column(
                modifier = Modifier
                    .width(70.dp)
                    .clip(MaterialTheme.shapes.large)
                    .border(
                        width = borderWidth,
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.large
                    ),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                listOf(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.secondary,
                    MaterialTheme.colorScheme.tertiary,
                    MaterialTheme.colorScheme.tertiaryContainer,
                    MaterialTheme.colorScheme.secondaryContainer,
                    MaterialTheme.colorScheme.primaryContainer,
                ).fastForEach {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                            .background(it)
                    )
                }
            }
        }

        Spacer(Modifier.size(8.dp))

        Text(
            text = stringResource(paletteStyle.nameRes),
            style = MaterialTheme.typography.bodyMedium,
            color = if (selected) {
                MaterialTheme.colorScheme.primary
            } else MaterialTheme.colorScheme.onSurface
        )
    }
}