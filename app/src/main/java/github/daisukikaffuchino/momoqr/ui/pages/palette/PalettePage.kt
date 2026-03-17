package github.daisukikaffuchino.momoqr.ui.pages.palette

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.ui.components.TopAppBarScaffold
import github.daisukikaffuchino.momoqr.ui.theme.Defaults
import kotlin.math.roundToInt

private enum class DotShape(val label: String) {
    Square("方形"),
    Circle("圆形")
}

@Composable
fun PalettePage(
    modifier: Modifier = Modifier,
    onNavigateUp: () -> Unit,
) {
    // --- 临时本地状态，后续可替换为 ViewModel 状态 ---
    var darkColor by remember { mutableStateOf(Color(0xFF111111)) }
    var lightColor by remember { mutableStateOf(Color(0xFFFFFFFF)) }
    var backgroundColor by remember { mutableStateOf(Color(0xFFF5F5F5)) }
    var pickColorFromBackground by remember { mutableStateOf(false) }

    var dotShapeIndex by remember { mutableIntStateOf(0) } // 0: square, 1: circle
    var dotScale by remember { mutableFloatStateOf(0.8f) } // 0.1..1.0, step 0.05

    var backgroundAlpha by remember { mutableFloatStateOf(1.0f) } // 0.1..1.0, step 0.1

    TopAppBarScaffold(
        title = stringResource(R.string.label_generate_color_palette),
        onBack = onNavigateUp,
        modifier = modifier
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Defaults.screenHorizontalPadding)
                .padding(top = Defaults.screenVerticalPadding, bottom = Defaults.screenVerticalPadding)
        ) {
            item {
                SectionCard(title = "颜色设置") {
                    ColorSettingRow(
                        title = "深色",
                        color = darkColor,
                        onClick = {
                            // TODO 打开取色器
                            darkColor = randomPreviewColor()
                        }
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    ColorSettingRow(
                        title = "浅色",
                        color = lightColor,
                        onClick = {
                            // TODO 打开取色器
                            lightColor = randomPreviewColor()
                        }
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    ColorSettingRow(
                        title = "背景色",
                        color = backgroundColor,
                        onClick = {
                            // TODO 打开取色器
                            backgroundColor = randomPreviewColor()
                        }
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = pickColorFromBackground,
                            onCheckedChange = { pickColorFromBackground = it }
                        )
                        Text(text = "从背景图取色")
                    }
                }
            }

            item {
                SectionCard(title = "数据点样式") {
                    Text(
                        text = "形状",
                        style = MaterialTheme.typography.titleSmall
                    )

                    val options = DotShape.entries
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        options.forEachIndexed { index, item ->
                            SegmentedButton(
                                selected = dotShapeIndex == index,
                                onClick = { dotShapeIndex = index },
                                shape = androidx.compose.material3.SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = options.size
                                )
                            ) {
                                Text(item.label)
                            }
                        }
                    }

                    Text(
                        text = "数据点比例：${"%.2f".format(dotScale)}",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(top = 16.dp)
                    )

                    Slider(
                        value = dotScale,
                        onValueChange = { dotScale = snapToStep(it, 0.1f, 0.05f) },
                        valueRange = 0.1f..1.0f,
                        // (1.0 - 0.1) / 0.05 = 18 个区间 => steps = 17
                        steps = 17,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            item {
                SectionCard(title = "Logo / 背景图") {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                // TODO 选择 Logo
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("设置 Logo")
                        }

                        Button(
                            onClick = {
                                // TODO 选择背景图
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("设置背景图")
                        }
                    }

                    Text(
                        text = "背景图透明度：${"%.1f".format(backgroundAlpha)}",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(top = 16.dp)
                    )

                    Slider(
                        value = backgroundAlpha,
                        onValueChange = { backgroundAlpha = snapToStep(it, 0.1f, 0.1f) },
                        valueRange = 0.1f..1.0f,
                        // (1.0 - 0.1) / 0.1 = 9 个区间 => steps = 8
                        steps = 8,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )

            content()
        }
    }
}

@Composable
private fun ColorSettingRow(
    title: String,
    color: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(color, shape = MaterialTheme.shapes.small)
            )
            TextButton(onClick = onClick) {
                Text(color.toHexString())
            }
        }
    }
}

private fun Color.toHexString(): String {
    return "#%08X".format(this.toArgb())
}

private fun snapToStep(value: Float, min: Float, step: Float): Float {
    val steps = ((value - min) / step).roundToInt()
    return min + steps * step
}

// 临时演示色，后续删掉
private fun randomPreviewColor(): Color {
    val colors = listOf(
        Color(0xFF111111),
        Color(0xFFFFFFFF),
        Color(0xFF4CAF50),
        Color(0xFF2196F3),
        Color(0xFFFF9800),
        Color(0xFFE91E63),
    )
    return colors.random()
}
