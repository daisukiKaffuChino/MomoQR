package github.daisukikaffuchino.momoqr.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import com.github.sumimakito.awesomeqr.AwesomeQrRenderer
import com.github.sumimakito.awesomeqr.option.RenderOption
import com.github.sumimakito.awesomeqr.option.background.StillBackground
import com.github.sumimakito.awesomeqr.option.color.Color
import com.github.sumimakito.awesomeqr.option.logo.Logo
import github.daisukikaffuchino.momoqr.logic.model.QRCodeECL
import androidx.core.graphics.createBitmap

data class QrAppearanceOptions(
    val darkArgb: Int = 0xFF000000.toInt(),
    val lightArgb: Int = 0xFFFFFFFF.toInt(),
    val backgroundArgb: Int = 0xFFFFFFFF.toInt(),
    val autoColor: Boolean = false,
    val roundedPatterns: Boolean = false,
    val patternScale: Float = 1.0f,
    val logoBitmap: Bitmap? = null,
    val backgroundBitmap: Bitmap? = null,
    val backgroundAlpha: Float = 1f,
    val borderWidth: Int = 20,
)

object QrGenerateUtil {

    fun generateQrBitmap(
        content: String,
        eclFloat: Float,
        qrSize: Int,
        appearance: QrAppearanceOptions = QrAppearanceOptions(),
        onSuccess: (Bitmap) -> Unit,
        onError: (Throwable) -> Unit = {}
    ) {
        val codeColor = Color()
        codeColor.light = appearance.lightArgb
        codeColor.dark = appearance.darkArgb
        codeColor.background = appearance.backgroundArgb
        codeColor.auto = appearance.autoColor

        val preparedBackground = appearance.backgroundBitmap?.let {
            if (appearance.backgroundAlpha >= 0.999f) {
                it
            } else {
                applyAlpha(it, appearance.backgroundAlpha)
            }
        }

        val renderOption = RenderOption().apply {
            this.content = content
            size = qrSize
            borderWidth = appearance.borderWidth
            clearBorder = false
            ecl = QRCodeECL.fromFloat(eclFloat).toZXingLevel()
            color = codeColor
            roundedPatterns = appearance.roundedPatterns
            patternScale = appearance.patternScale

            appearance.logoBitmap?.let { logoBitmap ->
                logo = Logo().apply {
                    bitmap = logoBitmap
                }
            }

            preparedBackground?.let { backgroundBitmap ->
                background = StillBackground().apply {
                    bitmap = backgroundBitmap
                }
            }
        }

        AwesomeQrRenderer.renderAsync(
            renderOption,
            { result ->
                releasePreparedBackground(preparedBackground, appearance.backgroundBitmap)
                val bitmap = result.bitmap
                if (bitmap != null) {
                    onSuccess(bitmap)
                } else {
                    onError(IllegalStateException("QR bitmap is null"))
                }
            },
            { exception ->
                releasePreparedBackground(preparedBackground, appearance.backgroundBitmap)
                onError(exception)
            }
        )
    }

    private fun releasePreparedBackground(
        preparedBackground: Bitmap?,
        originalBackground: Bitmap?,
    ) {
        if (
            preparedBackground != null &&
            preparedBackground !== originalBackground &&
            !preparedBackground.isRecycled
        ) {
            preparedBackground.recycle()
        }
    }

    private fun applyAlpha(source: Bitmap, alpha: Float): Bitmap {
        val safeAlpha = alpha.coerceIn(0f, 1f)
        val output = createBitmap(source.width, source.height)
        val canvas = Canvas(output)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.alpha = (safeAlpha * 255).toInt()
        }
        canvas.drawBitmap(source, 0f, 0f, paint)
        return output
    }
}
