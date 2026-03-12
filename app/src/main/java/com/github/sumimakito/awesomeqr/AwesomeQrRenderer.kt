package com.github.sumimakito.awesomeqr

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import androidx.core.graphics.createBitmap
import androidx.core.graphics.get
import androidx.core.graphics.scale
import com.github.sumimakito.awesomeqr.option.RenderOption
import com.github.sumimakito.awesomeqr.option.background.BlendBackground
import com.github.sumimakito.awesomeqr.option.background.StillBackground
import com.github.sumimakito.awesomeqr.util.RectUtils
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.google.zxing.qrcode.encoder.ByteMatrix
import com.google.zxing.qrcode.encoder.Encoder
import com.google.zxing.qrcode.encoder.QRCode
import java.util.Hashtable
import kotlin.math.roundToInt

class AwesomeQrRenderer {
    companion object {
        private const val BYTE_EPT = 0x0.toByte()
        private const val BYTE_DTA = 0x1.toByte()
        private const val BYTE_POS = 0x2.toByte()
        private const val BYTE_AGN = 0x3.toByte()
        private const val BYTE_TMG = 0x4.toByte()
        private const val BYTE_PTC = 0x5.toByte()

        @JvmStatic
        @Throws(Exception::class)
        fun render(renderOptions: RenderOption): RenderResult {
             if (renderOptions.background is BlendBackground && renderOptions.background!!.bitmap != null) {
                val background = renderOptions.background as BlendBackground
                val fallbackBitmap = background.bitmap ?: throw IllegalArgumentException("Background bitmap is null")
                var clippedBackground: Bitmap? = null
                if (background.clippingRect != null) {
                    clippedBackground = Bitmap.createBitmap(
                        fallbackBitmap,
                        background.clippingRect!!.left.toFloat().roundToInt(),
                        background.clippingRect!!.top.toFloat().roundToInt(),
                        background.clippingRect!!.width().toFloat().roundToInt(),
                        background.clippingRect!!.height().toFloat().roundToInt()
                    )
                }
                val rendered = renderFrame(renderOptions, clippedBackground ?: fallbackBitmap)
                clippedBackground?.recycle()
                val scaledBoundingRects = scaleImageBoundingRectByClippingRect(fallbackBitmap, renderOptions.size, background.clippingRect)
                val fullRendered = fallbackBitmap.scale(
                    scaledBoundingRects[0].width(),
                    scaledBoundingRects[0].height()
                )
                val fullCanvas = Canvas(fullRendered)
                val paint = Paint()
                paint.isAntiAlias = true
                paint.color = renderOptions.color.background
                paint.isFilterBitmap = true
                fullCanvas.drawBitmap(rendered, Rect(0, 0, rendered.width, rendered.height), scaledBoundingRects[1], paint)
                return RenderResult(fullRendered, null, RenderResult.OutputType.Blend)
            } else if (renderOptions.background is StillBackground) {
                val background = renderOptions.background as StillBackground
                val fallbackBitmap = background.bitmap ?: throw IllegalArgumentException("Background bitmap is null")
                var clippedBackground: Bitmap? = null
                if (background.clippingRect != null) {
                    clippedBackground = Bitmap.createBitmap(
                        fallbackBitmap,
                        background.clippingRect!!.left.toFloat().roundToInt(),
                        background.clippingRect!!.top.toFloat().roundToInt(),
                        background.clippingRect!!.width().toFloat().roundToInt(),
                        background.clippingRect!!.height().toFloat().roundToInt()
                    )
                }
                val rendered = renderFrame(renderOptions, clippedBackground ?: fallbackBitmap)
                clippedBackground?.recycle()
                return RenderResult(rendered, null, RenderResult.OutputType.Still)
            } else {
                return RenderResult(renderFrame(renderOptions, null), null, RenderResult.OutputType.Still)
            }
        }

        @JvmStatic
        fun renderAsync(renderOptions: RenderOption, resultCallback: ((RenderResult) -> Unit)?, errorCallback: ((Exception) -> Unit)?) {
            Thread {
                try {
                    val renderResult = render(renderOptions)
                    resultCallback?.invoke(renderResult)
                } catch (e: Exception) {
                    errorCallback?.invoke(e)
                }
            }.start()
        }

        @Throws(Exception::class)
        private fun renderFrame(renderOptions: RenderOption, backgroundFrame: Bitmap?): Bitmap {
            var backgroundFrameTemp = backgroundFrame
            if (renderOptions.content.isEmpty()) {
                throw IllegalArgumentException("Error: content is empty.")
            }
            if (renderOptions.size < 0 || renderOptions.borderWidth < 0 || renderOptions.size - 2 * renderOptions.borderWidth <= 0) {
                throw IllegalArgumentException("Invalid size or borderWidth.")
            }

            val byteMatrix = getByteMatrix(renderOptions.content, renderOptions.ecl)
                ?: throw NullPointerException("ByteMatrix based on content is null.")

            if (renderOptions.patternScale <= 0 || renderOptions.patternScale > 1) {
                throw IllegalArgumentException("Illegal pattern scale.")
            }

            if (renderOptions.logo != null && renderOptions.logo!!.bitmap != null) {
                val logo = renderOptions.logo!!
                if (logo.scale <= 0 || logo.scale > 0.5 ||
                    logo.borderWidth < 0 || logo.borderWidth * 2 >= renderOptions.size ||
                    logo.borderRadius < 0
                ) throw IllegalArgumentException("Invalid logo settings.")
            }

            if (backgroundFrameTemp == null &&
                (renderOptions.background is StillBackground || renderOptions.background is BlendBackground)
            ) {
                backgroundFrameTemp = renderOptions.background?.bitmap
            }

            val innerRenderedSize = renderOptions.size - 2 * renderOptions.borderWidth
            val nCount = byteMatrix.width
            val nSize = Math.round(innerRenderedSize.toFloat() / nCount)
            val unscaledInnerRenderSize = nSize * nCount
            val unscaledFullRenderSize = unscaledInnerRenderSize + 2 * renderOptions.borderWidth

            val backgroundDrawingRect = Rect(
                if (!renderOptions.clearBorder) 0 else renderOptions.borderWidth,
                if (!renderOptions.clearBorder) 0 else renderOptions.borderWidth,
                unscaledFullRenderSize - renderOptions.borderWidth * if (renderOptions.clearBorder) 1 else 0,
                unscaledFullRenderSize - renderOptions.borderWidth * if (renderOptions.clearBorder) 1 else 0
            )

            val unscaledFullRenderedBitmap =
                createBitmap(unscaledFullRenderSize, unscaledFullRenderSize)

            if (renderOptions.color.auto && backgroundFrame != null) {
                renderOptions.color.light = -0x1
                renderOptions.color.dark = getDominantColor(backgroundFrame)
            }

            val paint = Paint().apply { isAntiAlias = true }
            val paintBackground = Paint().apply {
                isAntiAlias = true
                color = renderOptions.color.background
                style = Paint.Style.FILL
            }
            val paintDark = Paint().apply {
                color = renderOptions.color.dark
                isAntiAlias = true
                style = Paint.Style.FILL
            }
            val paintLight = Paint().apply {
                color = renderOptions.color.light
                isAntiAlias = true
                style = Paint.Style.FILL
            }
            val paintProtector = Paint().apply {
                color = Color.argb(120, 255, 255, 255)
                isAntiAlias = true
                style = Paint.Style.FILL
            }

            val unscaledCanvas = Canvas(unscaledFullRenderedBitmap)
            unscaledCanvas.drawColor(Color.WHITE)
            unscaledCanvas.drawRect(
                backgroundDrawingRect,
                paintBackground
            )

            if (backgroundFrame != null && renderOptions.background != null) {
                paint.alpha = Math.round(255 * renderOptions.background!!.alpha)
                unscaledCanvas.drawBitmap(backgroundFrame, null, backgroundDrawingRect, paint)
            }
            paint.alpha = 255

            for (row in 0 until byteMatrix.height) {
                for (col in 0 until byteMatrix.width) {
                    val x = renderOptions.borderWidth + col * nSize
                    val y = renderOptions.borderWidth + row * nSize
                    val value = byteMatrix.get(col, row)
                    val paintToUse = when (value) {
                        BYTE_AGN, BYTE_POS, BYTE_TMG -> paintDark
                        BYTE_PTC -> paintProtector
                        BYTE_EPT -> if (renderOptions.roundedPatterns) {
                            unscaledCanvas.drawCircle(
                                x + nSize / 2f,
                                y + nSize / 2f,
                                renderOptions.patternScale * nSize / 2f,
                                paintLight
                            )
                            continue
                        } else paintLight
                        BYTE_DTA -> if (renderOptions.roundedPatterns) {
                            unscaledCanvas.drawCircle(
                                x + nSize / 2f,
                                y + nSize / 2f,
                                renderOptions.patternScale * nSize / 2f,
                                paintDark
                            )
                            continue
                        } else paintDark
                        else -> null
                    }
                    paintToUse?.let {
                        unscaledCanvas.drawRect(x.toFloat(), y.toFloat(), (x + nSize).toFloat(), (y + nSize).toFloat(), it)
                    }
                }
            }

            if (renderOptions.logo?.bitmap != null) {
                val logo = renderOptions.logo!!
                val logoScaledSize = (unscaledInnerRenderSize * logo.scale).toInt()
                val logoScaled = logo.bitmap!!.scale(logoScaledSize, logoScaledSize)
                val logoOpt = createBitmap(logoScaled.width, logoScaled.height)
                val logoCanvas = Canvas(logoOpt)
                val logoRect = Rect(0, 0, logoScaled.width, logoScaled.height)
                val logoPaint = Paint().apply { isAntiAlias = true }
                val logoRectF = RectF(logoRect)
                logoPaint.color = -0x1
                logoCanvas.drawARGB(0, 0, 0, 0)
                logoCanvas.drawRoundRect(logoRectF, logo.borderRadius.toFloat(), logo.borderRadius.toFloat(), logoPaint)
                logoPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
                logoCanvas.drawBitmap(logoScaled, logoRect, logoRect, logoPaint)
                logoPaint.color = renderOptions.color.light
                logoPaint.style = Paint.Style.STROKE
                logoPaint.strokeWidth = logo.borderWidth.toFloat()
                logoCanvas.drawRoundRect(logoRectF, logo.borderRadius.toFloat(), logo.borderRadius.toFloat(), logoPaint)
                unscaledCanvas.drawBitmap(logoOpt,
                    (0.5 * (unscaledFullRenderedBitmap.width - logoOpt.width)).toFloat(),
                    (0.5 * (unscaledFullRenderedBitmap.height - logoOpt.height)).toFloat(),
                    paint)
            }

            val renderedScaledBitmap = createBitmap(renderOptions.size, renderOptions.size)
            val scaledCanvas = Canvas(renderedScaledBitmap)
            scaledCanvas.drawBitmap(unscaledFullRenderedBitmap, null, Rect(0, 0, renderedScaledBitmap.width, renderedScaledBitmap.height), paint)

            var renderedResultBitmap: Bitmap = renderedScaledBitmap

            if (renderOptions.background is BlendBackground) {
                renderedResultBitmap =
                    createBitmap(renderedScaledBitmap.width, renderedScaledBitmap.height)
                val finalRenderedCanvas = Canvas(renderedResultBitmap)
                val finalClippingRect = Rect(0, 0, renderedScaledBitmap.width, renderedScaledBitmap.height)
                val finalClippingRectF = RectF(finalClippingRect)
                val finalClippingPaint = Paint().apply {
                    isAntiAlias = true
                    color = -0x1
                    style = Paint.Style.FILL
                }
                finalRenderedCanvas.drawARGB(0, 0, 0, 0)
                finalRenderedCanvas.drawRoundRect(finalClippingRectF, (renderOptions.background as BlendBackground).borderRadius.toFloat(), (renderOptions.background as BlendBackground).borderRadius.toFloat(), finalClippingPaint)
                finalClippingPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
                finalRenderedCanvas.drawBitmap(renderedScaledBitmap, null, finalClippingRect, finalClippingPaint)
                renderedScaledBitmap.recycle()
            }

            unscaledFullRenderedBitmap.recycle()
            return renderedResultBitmap
        }

                private fun getByteMatrix(contents: String, errorCorrectionLevel: ErrorCorrectionLevel): ByteMatrix? {
            try {
                val qrCode = getProtoQrCode(contents, errorCorrectionLevel)
                val agnCenter = qrCode.version.alignmentPatternCenters
                val byteMatrix = qrCode.matrix
                val matSize = byteMatrix.width
                for (row in 0 until matSize) {
                    for (col in 0 until matSize) {
                        if (isTypeAGN(col, row, agnCenter, true)) {
                            if (byteMatrix.get(col, row) != BYTE_EPT) {
                                byteMatrix.set(col, row, BYTE_AGN)
                            } else {
                                byteMatrix.set(col, row, BYTE_PTC)
                            }
                        } else if (isTypePOS(col, row, matSize, true)) {
                            if (byteMatrix.get(col, row) != BYTE_EPT) {
                                byteMatrix.set(col, row, BYTE_POS)
                            } else {
                                byteMatrix.set(col, row, BYTE_PTC)
                            }
                        } else if (isTypeTMG(col, row, matSize)) {
                            if (byteMatrix.get(col, row) != BYTE_EPT) {
                                byteMatrix.set(col, row, BYTE_TMG)
                            } else {
                                byteMatrix.set(col, row, BYTE_PTC)
                            }
                        }

                        if (isTypePOS(col, row, matSize, false)) {
                            if (byteMatrix.get(col, row) == BYTE_EPT) {
                                byteMatrix.set(col, row, BYTE_PTC)
                            }
                        }
                    }
                }
                return byteMatrix
            } catch (e: WriterException) {
                e.printStackTrace()
            }

            return null
        }

        /**
         * @param contents             Contents to encode.
         * @param errorCorrectionLevel ErrorCorrectionLevel
         * @return QR code object.
         * @throws WriterException Refer to the messages below.
         */
        @Throws(WriterException::class)
        private fun getProtoQrCode(contents: String, errorCorrectionLevel: ErrorCorrectionLevel): QRCode {
            if (contents.isEmpty()) {
                throw IllegalArgumentException("Found empty content.")
            }
            val hintMap = Hashtable<EncodeHintType, Any>()
            hintMap[EncodeHintType.CHARACTER_SET] = "UTF-8"
            hintMap[EncodeHintType.ERROR_CORRECTION] = errorCorrectionLevel
            return Encoder.encode(contents, errorCorrectionLevel, hintMap)
        }

        private fun isTypeAGN(x: Int, y: Int, agnCenter: IntArray, edgeOnly: Boolean): Boolean {
            if (agnCenter.isEmpty()) return false
            val edgeCenter = agnCenter[agnCenter.size - 1]
            for (agnY in agnCenter) {
                for (agnX in agnCenter) {
                    if (edgeOnly && agnX != 6 && agnY != 6 && agnX != edgeCenter && agnY != edgeCenter)
                        continue
                    if (agnX == 6 && agnY == 6 || agnX == 6 && agnY == edgeCenter || agnY == 6 && agnX == edgeCenter)
                        continue
                    if (x >= agnX - 2 && x <= agnX + 2 && y >= agnY - 2 && y <= agnY + 2)
                        return true
                }
            }
            return false
        }

        private fun isTypePOS(x: Int, y: Int, size: Int, inner: Boolean): Boolean {
            return if (inner) {
                x < 7 && (y < 7 || y >= size - 7) || x >= size - 7 && y < 7
            } else {
                x <= 7 && (y <= 7 || y >= size - 8) || x >= size - 8 && y <= 7
            }
        }

        private fun isTypeTMG(x: Int, y: Int, size: Int): Boolean {
            return y == 6 && x >= 8 && x < size - 8 || x == 6 && y >= 8 && y < size - 8
        }

        private fun scaleBitmap(src: Bitmap, dst: Bitmap) {
            val cPaint = Paint()
            cPaint.isAntiAlias = true
            cPaint.isDither = true
            cPaint.isFilterBitmap = true

            val ratioX = dst.width / src.width.toFloat()
            val ratioY = dst.height / src.height.toFloat()
            val middleX = dst.width * 0.5f
            val middleY = dst.height * 0.5f

            val scaleMatrix = Matrix()
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
            val canvas = Canvas(dst)
            canvas.setMatrix(scaleMatrix)
            canvas.drawBitmap(src, middleX - src.width / 2,
                    middleY - src.height / 2, cPaint)
        }

        private fun getDominantColor(bitmap: Bitmap): Int {
            val newBitmap = bitmap.scale(8, 8)
            var red = 0
            var green = 0
            var blue = 0
            var c = 0
            var r: Int
            var g: Int
            var b: Int
            for (y in 0 until newBitmap.height) {
                for (x in 0 until newBitmap.height) {
                    val color = newBitmap[x, y]
                    r = color shr 16 and 0xFF
                    g = color shr 8 and 0xFF
                    b = color and 0xFF
                    if (r > 200 || g > 200 || b > 200) continue
                    red += r
                    green += g
                    blue += b
                    c++
                }
            }
            newBitmap.recycle()
            if (c == 0) {
                // got a bitmap with no pixels in it
                // avoid the "divide by zero" error
                // but WHO DARES GIMME AN EMPTY BITMAP?
                return -0x1000000
            } else {
                val finalRed = 0.coerceAtLeast(0xFF.coerceAtMost(red / c))
                val finalGreen = 0.coerceAtLeast(0xFF.coerceAtMost(green / c))
                val finalBlue = 0.coerceAtLeast(0xFF.coerceAtMost(blue / c))

                val hsv = FloatArray(3)
                Color.RGBToHSV(finalRed, finalGreen, finalBlue, hsv)
                hsv[2] = hsv[2].coerceAtLeast(0.7f)

                return 0xFF shl 24 or Color.HSVToColor(hsv) // (0xFF << 24) | (red << 16) | (green << 8) | blue;
            }
        }

        // returns [finalBoundingRect, newClippingRect]
        private fun scaleImageBoundingRectByClippingRect(bitmap: Bitmap, size: Int, clippingRect: Rect?): Array<Rect> {
            if (clippingRect == null) return scaleImageBoundingRectByClippingRect(bitmap, size, Rect(0, 0, bitmap.width, bitmap.height))
            if (clippingRect.width() != clippingRect.height() || clippingRect.width() <= size) {
                return arrayOf(Rect(0, 0, bitmap.width, bitmap.height), clippingRect)
            }
            val clippingSize = clippingRect.width().toFloat()
            val scalingRatio = size / clippingSize
            return arrayOf(
                    RectUtils.round(RectF(
                            0f, 0f,
                            bitmap.width * scalingRatio, bitmap.height * scalingRatio)
                    ),
                    RectUtils.round(RectF(
                            clippingRect.left * scalingRatio, clippingRect.top * scalingRatio,
                            clippingRect.right * scalingRatio, clippingRect.bottom * scalingRatio)
                    )
            )
        }
    }
}
