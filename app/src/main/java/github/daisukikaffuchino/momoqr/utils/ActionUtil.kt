package github.daisukikaffuchino.momoqr.utils

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.UriHandler
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.logic.model.SearchEngine
import java.io.BufferedOutputStream
import java.io.FileOutputStream
import java.io.IOException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import androidx.core.net.toUri

@SuppressLint("ServiceCast")
fun Context.copyToClipboard(text: String) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("text", text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(this, R.string.toast_copied, Toast.LENGTH_SHORT).show()
}

fun buildSearchUrl(text: String, engine: SearchEngine): String {
    val encoded = URLEncoder.encode(text, StandardCharsets.UTF_8.toString())
    return when (engine) {
        SearchEngine.GOOGLE -> "https://www.google.com/search?q=$encoded"
        SearchEngine.BING -> "https://www.bing.com/search?q=$encoded"
        SearchEngine.YANDEX -> "https://yandex.com/search/?text=$encoded"
        SearchEngine.STARTPAGE -> "https://www.startpage.com/search?q=$encoded"
        SearchEngine.DUCKDUCKGO -> "https://duckduckgo.com/?q=$encoded"
        SearchEngine.BAIDU -> "https://www.baidu.com/s?wd=$encoded"
    }
}

fun Context.shareText(text: String, title: String = "") {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
        if (title.isNotEmpty()) {
            putExtra(Intent.EXTRA_SUBJECT, title)
        }
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    startActivity(Intent.createChooser(intent, null).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    })
}

@Composable
fun Context.rememberBitmapSaver(
    notAskForSavePath: Boolean,
    onSaveSuccess: (String) -> Unit = {},
    onSaveFailed: () -> Unit = {}
): (Bitmap) -> Unit {
    var pendingBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val saveLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("image/png")
    ) { uri ->
        val bitmap = pendingBitmap
        if (uri != null && bitmap != null) {
            val result = saveBitmapViaUri(uri, bitmap)
            if (result != null)
                onSaveSuccess(result)
            else
                onSaveFailed()
        } else onSaveFailed()
        pendingBitmap = null
    }

    return remember(notAskForSavePath, saveLauncher) {
        { bitmap: Bitmap ->
            if (notAskForSavePath) {
                val result = saveBitmapDirectly(bitmap)
                if (result != null)
                    onSaveSuccess(result)
                else
                    onSaveFailed()
            } else {
                pendingBitmap = bitmap
                val fileName = "QR${System.currentTimeMillis()}.png"
                saveLauncher.launch(fileName)
            }
        }
    }
}

private fun Context.saveBitmapDirectly(bitmap: Bitmap): String? {
    val nowTime = System.currentTimeMillis()
    val displayName = "QR$nowTime.png"

    val values = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
        put(
            MediaStore.MediaColumns.RELATIVE_PATH,
            "${Environment.DIRECTORY_PICTURES}/MomoQR"
        )
    }

    val resolver = contentResolver
    val uri: Uri =
        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) ?: return null

    return try {
        resolver.openOutputStream(uri)?.use { outputStream ->
            if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)) {
                return null
            }
        } ?: return null

        "/storage/emulated/0/Pictures/MomoQR/$displayName"
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

private fun Context.saveBitmapViaUri(uri: Uri, bitmap: Bitmap): String? {
    var pfd: ParcelFileDescriptor? = null
    return try {
        pfd = contentResolver.openFileDescriptor(uri, "w")
        if (pfd == null) return null

        BufferedOutputStream(FileOutputStream(pfd.fileDescriptor)).use { bos ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos)
            bos.flush()
        }

        queryDisplayName(contentResolver, uri).orEmpty()
    } catch (e: IOException) {
        e.fillInStackTrace()
        null
    } finally {
        try {
            pfd?.close()
        } catch (e: IOException) {
            e.fillInStackTrace()
        }
    }
}

private fun queryDisplayName(contentResolver: ContentResolver, uri: Uri): String? {
    val projection = arrayOf(MediaStore.MediaColumns.DISPLAY_NAME)
    val cursor: Cursor? = contentResolver.query(uri, projection, null, null, null)

    cursor?.use {
        if (it.moveToFirst()) {
            val index = it.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
            if (index != -1) return it.getString(index)
        }
    }
    return null
}

fun Context.launchWeChat() {
    try {
        val intent = packageManager.getLaunchIntentForPackage("com.tencent.mm")
        if (intent != null)
            startActivity(intent)
        else
            Toast.makeText(this, R.string.toast_not_installed_wechat, Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

object LinkOpener {
    fun open(
        context: Context,
        uriHandler: UriHandler,
        url: String,
        useCustomTabs: Boolean = true
    ) {
        val normalizedUrl = normalizeUrl(url) ?: return

        if (useCustomTabs) {
            val opened = try {
                val customTabsIntent = CustomTabsIntent.Builder()
                    .setShowTitle(true)
                    .build()
                customTabsIntent.launchUrl(context, normalizedUrl.toUri())
                true
            } catch (_: Exception) {
                false
            }

            if (opened) return
        }

        try {
            uriHandler.openUri(normalizedUrl)
        } catch (_: Exception) {
        }
    }

    private fun normalizeUrl(url: String): String? {
        val trimmed = url.trim()
        if (trimmed.isEmpty()) return null

        return if (
            trimmed.startsWith("http://", ignoreCase = true) ||
            trimmed.startsWith("https://", ignoreCase = true)
        ) {
            trimmed
        } else {
            "https://$trimmed"
        }
    }

    fun isValidUrl(text: String): Boolean {
        return try {
            val uri = text.toUri()
            uri.scheme == "http" || uri.scheme == "https"
        } catch (_: Exception) {
            false
        }
    }
}