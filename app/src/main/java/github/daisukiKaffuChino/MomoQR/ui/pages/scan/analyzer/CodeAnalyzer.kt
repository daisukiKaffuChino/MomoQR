package github.daisukikaffuchino.momoqr.ui.pages.scan.analyzer

import github.daisukikaffuchino.momoqr.ui.pages.scan.components.RATIO
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.ReaderException
import com.google.zxing.common.HybridBinarizer
import java.nio.ByteBuffer
import kotlin.math.roundToInt

class CodeAnalyzer(
    private val codeDetector: CodeDetector,
    formats: List<BarcodeFormat>,
    private val enhancedPreprocess: Boolean
) : ImageAnalysis.Analyzer {

    private val reader = MultiFormatReader().apply {
        //Log.d("xxx3", "formats = $formats")
        val map = mapOf(
            DecodeHintType.POSSIBLE_FORMATS to formats,
            DecodeHintType.TRY_HARDER to true,
            DecodeHintType.CHARACTER_SET to "UTF-8"
        )
        setHints(map)
    }

    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()
        val data = ByteArray(remaining())
        get(data)
        return data
    }

    override fun analyze(image: ImageProxy) {
        if (image.planes.isEmpty()) {
            image.close()
            return
        }

        image.use { img ->
            val plane = img.planes[0]
            val tempImageData = plane.buffer.toByteArray()
            val imageData = if (enhancedPreprocess)
                enhanceContrast(tempImageData)
            else
                tempImageData

            val size = img.width.coerceAtMost(img.height) * RATIO

            val left = (img.width - size) / 2f
            val top = (img.height - size) / 2f

            analyse(
                yuvData = imageData,
                dataWidth = plane.rowStride,
                dataHeight = img.height,
                left = left.roundToInt(),
                top = top.roundToInt(),
                width = size.roundToInt(),
                height = size.roundToInt()
            )
        }
    }

    fun analyse(
        yuvData: ByteArray,
        dataWidth: Int,
        dataHeight: Int,
        left: Int,
        top: Int,
        width: Int,
        height: Int
    ) {
        try {
            val source = PlanarYUVLuminanceSource(
                yuvData,
                dataWidth,
                dataHeight,
                left,
                top,
                width,
                height,
                false
            )

            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
            reader.reset()
            try {
                val result = reader.decode(binaryBitmap)
                codeDetector.onDetected(result.text)
            } catch (_: ReaderException) {
                val invertedSource = source.invert()
                val invertedBinaryBitmap = BinaryBitmap(HybridBinarizer(invertedSource))
                reader.reset()
                try {
                    val result = reader.decode(invertedBinaryBitmap)
                    codeDetector.onDetected(result.text)
                } catch (_: ReaderException) {
                    // Not Found
                }
            }
        } catch (e: Exception) {
            codeDetector.onError(e)
        }
    }

    private fun enhanceContrast(data: ByteArray): ByteArray {
        val out = ByteArray(data.size)

        for (i in data.indices) {
            val v = data[i].toInt() and 0xff
            out[i] = if (v > 128) 255.toByte() else 0
        }

        return out
    }
}