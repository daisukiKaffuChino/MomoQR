package github.daisukikaffuchino.momoqr.utils

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import github.daisukikaffuchino.momoqr.ui.activities.MainActivity
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