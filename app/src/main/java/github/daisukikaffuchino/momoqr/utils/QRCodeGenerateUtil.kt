package github.daisukikaffuchino.momoqr.utils

import android.graphics.Bitmap
import com.github.sumimakito.awesomeqr.AwesomeQrRenderer
import com.github.sumimakito.awesomeqr.option.RenderOption
import com.github.sumimakito.awesomeqr.option.color.Color
import github.daisukikaffuchino.momoqr.logic.model.QRCodeECL

object QRCodeGenerateUtil {

    fun generateQrBitmap(
        content: String,
        eclFloat: Float,
        onSuccess: (Bitmap) -> Unit,
        onError: (Throwable) -> Unit = {}
    ) {
        val codeColor = Color()
        codeColor.light = 0xFFFFFFFF.toInt() // for blank spaces
        codeColor.dark = 0xFF000000.toInt() // for non-blank spaces
        codeColor.background = 0xFFFFFFFF.toInt() // for the background (will be overridden by background images, if set)
        codeColor.auto = false // set to true to automatically pick out colors from the background image (will only work if background image is present)

        val renderOption = RenderOption().apply {
            this.content = content
            size = 900
            borderWidth = 64
            ecl = QRCodeECL.fromFloat(eclFloat).toZXingLevel()
            color = codeColor
        }

        AwesomeQrRenderer.renderAsync(
            renderOption,
            { result ->
                val bitmap = result.bitmap
                if (bitmap != null) {
                    onSuccess(bitmap)
                } else {
                    onError(IllegalStateException("QR bitmap is null"))
                }
            },
            { exception ->
                onError(exception)
            }
        )
    }

}