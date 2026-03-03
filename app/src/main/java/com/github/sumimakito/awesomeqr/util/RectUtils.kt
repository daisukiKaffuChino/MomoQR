package com.github.sumimakito.awesomeqr.util

import android.graphics.Rect
import android.graphics.RectF
import kotlin.math.roundToInt

object RectUtils {
    fun round(rectF: RectF): Rect {
        return Rect(
            rectF.left.roundToInt(), rectF.top.roundToInt(), rectF.right.roundToInt(),
            rectF.bottom.roundToInt()
        )
    }
}
