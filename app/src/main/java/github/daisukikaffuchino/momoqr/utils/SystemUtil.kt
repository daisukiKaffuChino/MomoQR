package github.daisukikaffuchino.momoqr.utils

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.DisplayMetrics
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.core.os.LocaleListCompat
import github.daisukikaffuchino.momoqr.ui.activities.MainActivity
import java.text.DateFormat
import java.util.Date
import kotlin.system.exitProcess

fun ComponentActivity.configureEdgeToEdge() {
    enableEdgeToEdge()
    // Force the 3-button navigation bar to be transparent
    // See: https://developer.android.com/develop/ui/views/layout/edge-to-edge#create-transparent
    window.isNavigationBarContrastEnforced = false
}

fun Context.restartApp() {
    val intent = Intent(
        this,
        MainActivity::class.java
    ).apply {
        flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    this.startActivity(intent)
    exitProcess(0)
}

fun Context.appVersion(): String {
    val pkgInfo = this.packageManager.getPackageInfo(this.packageName, 0)
    val verName = pkgInfo.versionName
    val verCode = pkgInfo.longVersionCode.toInt()
    return "$verName ($verCode)"
}

fun setAppLanguage(code: String?) {
    if (code == null) {
        // 跟随系统
        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.getEmptyLocaleList()
        )
    } else {
        val locale = LocaleListCompat.forLanguageTags(code)
        AppCompatDelegate.setApplicationLocales(locale)
    }
}

fun getSystemInfo(context: Context): String {
    val pm = context.packageManager
    val packageInfo = pm.getPackageInfo(context.packageName, 0)

    val versionName = packageInfo.versionName ?: "unknown"
    val versionCode = packageInfo.longVersionCode

    val metrics: DisplayMetrics = context.resources.displayMetrics
    val density = metrics.density
    val densityDpi = metrics.densityDpi
    val width = metrics.widthPixels
    val height = metrics.heightPixels

    return buildString {

        appendLine("=== App ===")
        appendLine("Package: ${context.packageName}")
        appendLine("Version: $versionName ($versionCode)")
        appendLine()

        appendLine("=== Android ===")
        appendLine("Android: ${Build.VERSION.RELEASE}")
        appendLine("API Level: ${Build.VERSION.SDK_INT}")
        appendLine("Security Patch: ${Build.VERSION.SECURITY_PATCH}")
        appendLine()

        appendLine("=== Device ===")
        appendLine("Manufacturer: ${Build.MANUFACTURER}")
        appendLine("Brand: ${Build.BRAND}")
        appendLine("Model: ${Build.MODEL}")
        appendLine("Device: ${Build.DEVICE}")
        appendLine("Product: ${Build.PRODUCT}")
        appendLine("Board: ${Build.BOARD}")
        appendLine("Hardware: ${Build.HARDWARE}")
        appendLine()

        appendLine("=== CPU ===")
        appendLine("Supported ABIs: ${Build.SUPPORTED_ABIS.joinToString()}")
        appendLine()

        appendLine("=== Display ===")
        appendLine("Resolution: $width × $height")
        appendLine("Density: $density ($densityDpi dpi)")
        appendLine()

        appendLine("=== Build ===")
        appendLine("Build ID: ${Build.ID}")
        appendLine("Type: ${Build.TYPE}")
        appendLine("Tags: ${Build.TAGS}")
        appendLine("User: ${Build.USER}")
        appendLine("Host: ${Build.HOST}")
        appendLine("Time: ${
            DateFormat.getDateTimeInstance().format(Date(Build.TIME))
        }")
        appendLine()

        appendLine("Fingerprint:")
        appendLine(Build.FINGERPRINT)
    }
}

@Composable
fun keyboardAsState(): androidx.compose.runtime.State<Boolean> {
    val ime = WindowInsets.ime
    val density = LocalDensity.current

    return remember {
        derivedStateOf {
            ime.getBottom(density) > 0
        }
    }
}