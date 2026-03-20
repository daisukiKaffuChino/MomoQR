package github.daisukikaffuchino.momoqr.utils

import android.content.Context
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import github.daisukikaffuchino.momoqr.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * 绘制渐变边缘遮罩
 *
 * @param edgeWidth 渐变边缘宽度
 * @param maskColor 遮罩颜色
 * @param leftEdge 是否在左侧边缘添加遮罩（否即在右侧边缘添加）
 */
fun ContentDrawScope.drawFadedEdge(
    edgeWidth: Dp,
    maskColor: Color,
    leftEdge: Boolean
) {
    val edgeWidthPx = edgeWidth.toPx()
    drawRect(
        topLeft = Offset(if (leftEdge) 0f else size.width - edgeWidthPx, 0f),
        size = Size(edgeWidthPx, size.height),
        brush =
            Brush.horizontalGradient(
                colors = listOf(Color.Transparent, maskColor),
                startX = if (leftEdge) 0f else size.width,
                endX = if (leftEdge) edgeWidthPx else size.width - edgeWidthPx
            ),
        blendMode = BlendMode.DstIn
    )
}

fun Long?.toLocalDateString(displayYear: Boolean = true): String {
    if (this == null) return ""
    val pattern = if (displayYear)
        "yyyy-MM-dd HH:mm"
    else
        "MM-dd HH:mm"
    val format = SimpleDateFormat(pattern, Locale.getDefault())
    return format.format(Date(this))
}

fun Long?.toRelativeTimeString(context: Context): String {
    if (this == null) return ""

    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val diffDays = ((this - today) / (24 * 60 * 60 * 1000)).toInt()

    return when {
        diffDays == 0 -> context.getString(R.string.time_today)
        diffDays == -1 -> context.getString(R.string.time_yesterday)
        diffDays in -6..-2 -> context.getString(R.string.time_days_ago, -diffDays)
        diffDays in -29..-7 -> context.getString(R.string.time_weeks_ago, (-diffDays + 6) / 7)
        diffDays in -364..-30 -> context.getString(R.string.time_months_ago, (-diffDays + 29) / 30)
        diffDays <= -365 -> context.getString(R.string.time_years_ago, (-diffDays + 364) / 365)
        else -> context.getString(R.string.time_today)
    }
}

fun Color.toHexString(): String = "#%08X".format(toArgb())
