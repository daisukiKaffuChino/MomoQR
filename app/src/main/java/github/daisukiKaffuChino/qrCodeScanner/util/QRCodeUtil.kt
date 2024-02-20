package github.daisukiKaffuChino.qrCodeScanner.util

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.util.Log
import androidx.annotation.ColorInt
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.ChecksumException
import com.google.zxing.DecodeHintType
import com.google.zxing.EncodeHintType
import com.google.zxing.FormatException
import com.google.zxing.NotFoundException
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.Result
import com.google.zxing.WriterException
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader
import com.google.zxing.qrcode.QRCodeWriter
import java.io.IOException
import java.io.InputStream
import java.util.Hashtable

object QRCodeUtil {
    private const val TAG = "QRCodeUtil"

    fun createQRCodeBitmap(
        content: String,
        width: Int,
        height: Int,
        @ColorInt color_black: Int = Color.BLACK,
        @ColorInt color_white: Int = Color.WHITE,
    ): Bitmap? {
        if (width < 0 || height < 0) { // 宽和高都需要>=0
            return null
        }
        try {

            val hints: Hashtable<EncodeHintType, String> = Hashtable()

            hints[EncodeHintType.CHARACTER_SET] = "UTF-8" // 字符转码格式设置
            hints[EncodeHintType.ERROR_CORRECTION] = "H" // 容错级别设置
            hints[EncodeHintType.MARGIN] = "1" // 空白边距设置

            val bitMatrix =
                QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints)

            val pixels = IntArray(width * height)
            for (y in 0 until height) {
                for (x in 0 until width) {
                    if (bitMatrix[x, y]) {
                        pixels[y * width + x] = color_black // 黑色色块像素设置
                    } else {
                        pixels[y * width + x] = color_white // 白色色块像素设置
                    }
                }
            }
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
            return bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
        }
        return null
    }

    fun scanningImage(context: Context, uri: Uri): Result? {
        var result: Result? = null
        for (i in 1..4) {
            val scanBitmap = decodeUri(context, uri, i)
            val hints = Hashtable<DecodeHintType, String?>()
            hints[DecodeHintType.CHARACTER_SET] = "utf-8"
            val px = IntArray(scanBitmap!!.width * scanBitmap.height)
            scanBitmap.getPixels(px, 0, scanBitmap.width, 0, 0, scanBitmap.width, scanBitmap.height)
            val source = RGBLuminanceSource(scanBitmap.width, scanBitmap.height, px)
            val tempBitmap = BinaryBitmap(HybridBinarizer(source))
            val reader = QRCodeReader()
            try {
                result = reader.decode(tempBitmap, hints)
            } catch (e: NotFoundException) {
                e.printStackTrace()
            } catch (e: ChecksumException) {
                e.printStackTrace()
            } catch (e: FormatException) {
                e.printStackTrace()
            }
            scanBitmap.recycle()
            if (null != result) {
                return result
            }
        }
        return null
    }

    private fun decodeUri(context: Context, uri: Uri, scale: Int): Bitmap? {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        resolveUri(context, uri, options)
        options.inSampleSize = scale
        options.inJustDecodeBounds = false
        options.inPreferredConfig = Bitmap.Config.RGB_565
        var bitmap: Bitmap? = null
        try {
            bitmap = resolveUriForBitmap(context, uri, options)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return bitmap
    }

    private fun resolveUri(context: Context, uri: Uri?, options: BitmapFactory.Options) {
        if (uri == null) {
            return
        }
        val scheme = uri.scheme
        if (ContentResolver.SCHEME_CONTENT == scheme ||
            ContentResolver.SCHEME_FILE == scheme
        ) {
            var stream: InputStream? = null
            try {
                stream = context.contentResolver.openInputStream(uri)
                BitmapFactory.decodeStream(stream, null, options)
            } catch (e: java.lang.Exception) {
                Log.d(TAG, "Unable to open content: $uri", e)
            } finally {
                if (stream != null) {
                    try {
                        stream.close()
                    } catch (e: IOException) {
                        Log.d("resolveUri", "Unable to close content: $uri", e)
                    }
                }
            }
        } else if (ContentResolver.SCHEME_ANDROID_RESOURCE == scheme) {
            Log.d(TAG, "Unable to close content: $uri")
        } else {
            Log.d(TAG, "Unable to close content: $uri")
        }
    }

    private fun resolveUriForBitmap(
        context: Context,
        uri: Uri?,
        options: BitmapFactory.Options
    ): Bitmap? {
        if (uri == null) {
            return null
        }
        var bitmap: Bitmap? = null
        val scheme = uri.scheme
        if (ContentResolver.SCHEME_CONTENT == scheme ||
            ContentResolver.SCHEME_FILE == scheme
        ) {
            var stream: InputStream? = null
            try {
                stream = context.contentResolver.openInputStream(uri)
                bitmap = BitmapFactory.decodeStream(stream, null, options)
            } catch (e: java.lang.Exception) {
                Log.d("resolveUriForBitmap", "Unable to open content: $uri", e)
            } finally {
                if (stream != null) {
                    try {
                        stream.close()
                    } catch (e: IOException) {
                        Log.d("resolveUriForBitmap", "Unable to close content: $uri", e)
                    }
                }
            }
        } else if (ContentResolver.SCHEME_ANDROID_RESOURCE == scheme) {
            Log.d(TAG, "Unable to close content: $uri")
        } else {
            Log.d(TAG, "Unable to close content: $uri")
        }
        return bitmap
    }
}