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

object QRCodeReaderUtil {
    private const val TAG = "QRCodeReaderUtil"

    fun scanImageFromGallery(context: Context, uri: Uri, formats: List<BarcodeFormat>): Result? {
        val hints = mapOf<DecodeHintType, Any>(
            DecodeHintType.CHARACTER_SET to "UTF-8",
            DecodeHintType.TRY_HARDER to true
        )

        val mSizes = intArrayOf(1, 2, 4, 8)
        //val reader = QRCodeReader()
        val reader = MultiFormatReader().apply {
            setHints(
                mapOf(
                    DecodeHintType.POSSIBLE_FORMATS to formats,
                    DecodeHintType.TRY_HARDER to true,
                    DecodeHintType.CHARACTER_SET to "UTF-8"
                )
            )
        }

        for (mSize in mSizes) {
            val bitmap = decodeUri(context, uri, mSize) ?: continue

            try {
                decodeBitmap(bitmap, reader, hints)?.let { return it }
            } finally {
                if (!bitmap.isRecycled)
                    bitmap.recycle()
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

        return null
    }

    private fun decodeUri(
        context: Context,
        uri: Uri,
        mSize: Int
    ): Bitmap? {
        val bounds = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        resolveUri(context, uri, bounds)

        val options = BitmapFactory.Options().apply {
            inSampleSize = mSize
            inJustDecodeBounds = false
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }

        return try {
            resolveUriForBitmap(context, uri, options)
        } catch (e: Throwable) {
            Log.e(TAG, "decodeUri failed: $uri, mSize=$mSize", e)
            null
        }
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