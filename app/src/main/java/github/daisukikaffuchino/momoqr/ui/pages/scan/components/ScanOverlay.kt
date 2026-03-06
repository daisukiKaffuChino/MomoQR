package github.daisukikaffuchino.momoqr.ui.pages.scan.components

import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb

const val RATIO = 0.75f

@Composable
fun ScanOverlay() {
    val overlayColor = Color.Black.copy(alpha = 0.5f)
    val cornerColor = Color.White
    val cornerStrokeDp = 4f
    val cornerLengthDp = 96f
    val cornerRadiusDp = cornerLengthDp / 2

    /** GPT:
    这个正方形是从扫描框的绝对左上角 (left, top) 开始绘制的，而不是从圆角的圆心开始。

    为什么 cornerRadiusDp = 48f 能贴合？
    当 cornerRadiusDp = 48f 时，这个正方形的边长 cornerLengthDp = 96f，意味着：

    圆弧绘制区域的中心点在 (left + 48f, top + 48f) 处

            而圆角矩形的圆角圆心也在 (left + 48f, top + 48f) 处

            两者的半径都是 48f

    这样就完全重合了！
    */

    Canvas(modifier = Modifier.fillMaxSize()) {
        val overlayWidth = size.width
        val overlayHeight = size.height
        val viewfinderSize = minOf(overlayWidth, overlayHeight) * RATIO
        val cornerRadius = cornerRadiusDp * density
        val cornerLength = cornerLengthDp * density
        val stroke = cornerStrokeDp * density

        val left = (overlayWidth - viewfinderSize) / 2f
        val top = (overlayHeight - viewfinderSize) / 2f
        val right = left + viewfinderSize
        val bottom = top + viewfinderSize

        // Draw dimmed background with rounded rectangle cutout
        drawIntoCanvas { canvas ->
            val native = canvas.nativeCanvas

            val overlayPaint = Paint().apply {
                color = overlayColor.toArgb()
                style = Paint.Style.FILL
            }
            val clearPaint = Paint().apply {
                xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            }

            val saveCount = native.saveLayer(null, null)
            // draw full-screen dim overlay on the layer
            native.drawRect(0f, 0f, size.width, size.height, overlayPaint)

            // cut out rounded rectangle
            val rectF = RectF(left, top, right, bottom)
            native.drawRoundRect(rectF, cornerRadius, cornerRadius, clearPaint)

            native.restoreToCount(saveCount)
        }

        // Draw rounded corners using arcs
        val paint = Paint().apply {
            color = cornerColor.toArgb()
            style = Paint.Style.STROKE
            strokeWidth = stroke
            isAntiAlias = true
        }

        drawIntoCanvas { canvas ->
            val native = canvas.nativeCanvas

            // Top-left corner arc
            val topLeftRect = RectF(
                left,
                top,
                left + cornerLength,
                top + cornerLength
            )
            native.drawArc(topLeftRect, 180f, 90f, false, paint)

            // Top-right corner arc
            val topRightRect = RectF(
                right - cornerLength,
                top,
                right,
                top + cornerLength
            )
            native.drawArc(topRightRect, 270f, 90f, false, paint)

            // Bottom-right corner arc
            val bottomRightRect = RectF(
                right - cornerLength,
                bottom - cornerLength,
                right,
                bottom
            )
            native.drawArc(bottomRightRect, 0f, 90f, false, paint)

            // Bottom-left corner arc
            val bottomLeftRect = RectF(
                left,
                bottom - cornerLength,
                left + cornerLength,
                bottom
            )
            native.drawArc(bottomLeftRect, 90f, 90f, false, paint)
        }
    }
}