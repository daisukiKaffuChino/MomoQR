package github.daisukikaffuchino.momoqr.ui.pages.settings.components.appearance

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.graphics.shapes.toPath
import github.daisukikaffuchino.momoqr.ui.theme.MomoLogoFontFamily

@SuppressLint("ConfigurationScreenWidthHeight")
@Preview
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpressiveLogoCard(
    onClick: () -> Unit = {}
) {
    val configuration = LocalConfiguration.current
    val minScreenDimension = minOf(configuration.screenWidthDp, configuration.screenHeightDp).dp
    val shapeColorPrimary = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
    val strokeColorPrimary = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
    val shapeColorSecondary = MaterialTheme.colorScheme.secondary.copy(alpha = 0.015f)
    val strokeColorSecondary = MaterialTheme.colorScheme.secondary.copy(alpha = 0.04f)
    val strokeWidth = 6f

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                // --- 关键限制点 ---
                .widthIn(max = minOf(minScreenDimension, 400.dp)) // 限制最大宽度，防止横屏拉伸过大
                .fillMaxWidth()        // 在小屏上占满
                .aspectRatio(2f)       // 严格保持 2:1
                // ----------------
                .clip(MaterialTheme.shapes.largeIncreased)
                .border(
                    1.5.dp,
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                    MaterialTheme.shapes.largeIncreased
                ),
            onClick = onClick,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceBright
            )
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .clipToBounds()
            ) {
                val width = constraints.maxWidth.toFloat()
                val height = constraints.maxHeight.toFloat()

                // 在 remember 中转换 Path，避免每帧重复创建对象
                val twelveSidePath = remember {
                    MaterialShapes.Cookie12Sided.toPath().asComposePath()
                }
                val fourSidePath = remember {
                    MaterialShapes.Cookie4Sided.toPath().asComposePath()
                }
                val trianglePath = remember {
                    MaterialShapes.Triangle.toPath().asComposePath()
                }

                Canvas(modifier = Modifier.fillMaxSize()) {

                    // 1. 右上角：12-side cookie
                    withTransform({
                        translate(width * 0.65f, height * -0.3f) // 移动到右上角
                        scale(480f, 480f, pivot = Offset.Zero) // 放大
                        rotate(15f, pivot = Offset.Zero)       // 旋转
                    }) {
                        drawPath(twelveSidePath, shapeColorPrimary)
                        drawPath(
                            twelveSidePath,
                            strokeColorPrimary,
                            style = Stroke(strokeWidth / 200f)
                        ) // 缩放后描边也要除以倍数
                    }

                    // 2. 左侧中间：4-side cookie
                    withTransform({
                        translate(width * -0.12f, height * 0.4f) // 靠左半切
                        scale(300f, 300f, pivot = Offset.Zero)
                        rotate(-30f, pivot = Offset.Zero)
                    }) {
                        drawPath(fourSidePath, shapeColorPrimary)
                        drawPath(
                            fourSidePath,
                            strokeColorPrimary,
                            style = Stroke(strokeWidth / 180f)
                        )
                    }

                    // 3. 底部中间：Triangle
                    withTransform({
                        translate(width * 0.7f, height * 0.8f) // 靠下半切
                        scale(300f, 300f, pivot = Offset.Zero)
                        rotate(105f, pivot = Offset.Zero)
                    }) {
                        drawPath(trianglePath, shapeColorSecondary)
                        drawPath(
                            trianglePath,
                            strokeColorSecondary,
                            style = Stroke(strokeWidth / 150f)
                        )
                    }
                }
                Text(
                    text = "MomoQR",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontFamily = MomoLogoFontFamily,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.secondary,
                        letterSpacing = (-0.5).sp
                    )
                )
            }
        }
    }
}
