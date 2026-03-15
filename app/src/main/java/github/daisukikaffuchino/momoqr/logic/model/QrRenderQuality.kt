package github.daisukikaffuchino.momoqr.logic.model

import androidx.annotation.StringRes
import github.daisukikaffuchino.momoqr.R

enum class QrRenderQuality(val value: String) {
    HIGH_QUALITY("high_quality"),
    BALANCED("balanced"),
    HIGH_PERFORMANCE("high_performance");

    companion object {
        fun fromValue(value: String): QrRenderQuality {
            return entries.find { it.value == value } ?: BALANCED
        }
    }

    @StringRes
    fun labelRes(): Int = when (this) {
        HIGH_QUALITY -> R.string.qr_render_quality_high_quality
        BALANCED -> R.string.qr_render_quality_balanced
        HIGH_PERFORMANCE -> R.string.qr_render_quality_high_performance
    }

    fun getSize(): Int = when (this) {
        HIGH_PERFORMANCE -> 512
        BALANCED -> 800
        HIGH_QUALITY -> 1280
    }

}