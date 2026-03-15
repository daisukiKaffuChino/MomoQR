package github.daisukikaffuchino.momoqr.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.ChecksumException
import com.google.zxing.DecodeHintType
import com.google.zxing.FormatException
import com.google.zxing.MultiFormatReader
import com.google.zxing.NotFoundException
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.Reader
import com.google.zxing.Result
import com.google.zxing.common.GlobalHistogramBinarizer
import com.google.zxing.common.HybridBinarizer

object QrReaderUtil {
    private const val TAG = "QrReaderUtil"

    fun scanImageFromGallery(
        context: Context,
        uri: Uri,
        formats: List<BarcodeFormat>
    ): Result? {
        val hints = mapOf(
            DecodeHintType.TRY_HARDER to true,
            DecodeHintType.POSSIBLE_FORMATS to formats,
            DecodeHintType.CHARACTER_SET to "UTF-8"
        )

        val reader = MultiFormatReader().apply {
            setHints(hints)
        }

        // 按“最大边长”逐步降采样重试
        // 前面优先保留更多细节，后面优先保命和降低内存占用
        val maxSides = intArrayOf(1600, 1200, 900, 640)

        for (maxSide in maxSides) {
            val bitmap = decodeSampledBitmapFromUri(context, uri, maxSide) ?: continue

            try {
                decodeBitmap(bitmap, reader, hints)?.let { return it }
            } finally {
                if (!bitmap.isRecycled) {
                    bitmap.recycle()
                }
            }
        }

        return null
    }

    private fun decodeBitmap(
        bitmap: Bitmap,
        reader: Reader,
        hints: Map<DecodeHintType, Any>
    ): Result? {
        val width = bitmap.width
        val height = bitmap.height

        return try {
            val pixels = IntArray(width * height)
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

            val source = RGBLuminanceSource(width, height, pixels)

            val bitmapsToTry = listOf(
                BinaryBitmap(HybridBinarizer(source)),
                BinaryBitmap(GlobalHistogramBinarizer(source))
            )

            for (binaryBitmap in bitmapsToTry) {
                try {
                    return reader.decode(binaryBitmap, hints)
                } catch (_: NotFoundException) {
                } catch (_: ChecksumException) {
                } catch (_: FormatException) {
                } finally {
                    reader.reset()
                }
            }

            null
        } catch (oom: OutOfMemoryError) {
            Log.e(TAG, "decodeBitmap OOM: ${width}x$height", oom)
            null
        } catch (t: Throwable) {
            Log.e(TAG, "decodeBitmap failed", t)
            null
        }
    }

    private fun decodeSampledBitmapFromUri(
        context: Context,
        uri: Uri,
        maxSide: Int
    ): Bitmap? {
        val boundsOptions = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }

        resolveUri(context, uri, boundsOptions)

        val srcWidth = boundsOptions.outWidth
        val srcHeight = boundsOptions.outHeight

        if (srcWidth <= 0 || srcHeight <= 0) {
            Log.e(TAG, "decodeSampledBitmapFromUri: invalid bounds for $uri")
            return null
        }

        val sampleSize = calculateInSampleSize(
            width = srcWidth,
            height = srcHeight,
            maxSide = maxSide
        )

        val decodeOptions = BitmapFactory.Options().apply {
            inSampleSize = sampleSize
            inJustDecodeBounds = false
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }

        return try {
            resolveUriForBitmap(context, uri, decodeOptions)
        } catch (oom: OutOfMemoryError) {
            Log.e(TAG, "decodeSampledBitmapFromUri OOM: $uri, maxSide=$maxSide, sampleSize=$sampleSize", oom)
            null
        } catch (t: Throwable) {
            Log.e(TAG, "decodeSampledBitmapFromUri failed: $uri, maxSide=$maxSide, sampleSize=$sampleSize", t)
            null
        }
    }

    private fun calculateInSampleSize(
        width: Int,
        height: Int,
        maxSide: Int
    ): Int {
        var sampleSize = 1
        var currentWidth = width
        var currentHeight = height

        while (currentWidth > maxSide || currentHeight > maxSide) {
            currentWidth /= 2
            currentHeight /= 2
            sampleSize *= 2
        }

        return sampleSize.coerceAtLeast(1)
    }

    private fun resolveUri(
        context: Context,
        uri: Uri,
        options: BitmapFactory.Options
    ) {
        when (uri.scheme) {
            ContentResolver.SCHEME_CONTENT,
            ContentResolver.SCHEME_FILE -> {
                try {
                    context.contentResolver.openInputStream(uri)?.use { stream ->
                        BitmapFactory.decodeStream(stream, null, options)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Unable to open content: $uri", e)
                }
            }

            else -> Log.e(TAG, "Unsupported uri scheme: $uri")
        }
    }

    private fun resolveUriForBitmap(
        context: Context,
        uri: Uri,
        options: BitmapFactory.Options
    ): Bitmap? {
        return when (uri.scheme) {
            ContentResolver.SCHEME_CONTENT,
            ContentResolver.SCHEME_FILE -> {
                try {
                    context.contentResolver.openInputStream(uri)?.use { stream ->
                        BitmapFactory.decodeStream(stream, null, options)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Unable to decode bitmap: $uri", e)
                    null
                }
            }

            else -> {
                Log.e(TAG, "Unsupported uri scheme: $uri")
                null
            }
        }
    }
}