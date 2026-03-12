package github.daisukikaffuchino.momoqr.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.view.WindowCompat
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.logic.datastore.DataStoreManager
import github.daisukikaffuchino.momoqr.logic.model.DarkMode
import github.daisukikaffuchino.momoqr.logic.model.PaletteStyle
import github.daisukikaffuchino.momoqr.ui.pages.crash.CrashPage
import github.daisukikaffuchino.momoqr.ui.theme.MomoQRTheme
import github.daisukikaffuchino.momoqr.utils.VibrationUtil
import github.daisukikaffuchino.momoqr.utils.configureEdgeToEdge
import github.daisukikaffuchino.momoqr.constants.Constants

class CrashActivity : ComponentActivity(){
    companion object {
        const val BRAND_PREFIX = "Brand:      "
        const val MODEL_PREFIX = "Model:      "
        const val DEVICE_SDK_PREFIX = "Device SDK: "
        const val CRASH_TIME_PREFIX = "Crash time: "
        const val BEGINNING_CRASH = "======beginning of crash======"
    }

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        configureEdgeToEdge()
        super.onCreate(savedInstanceState)

        val crashLogs = intent.getStringExtra("logs")

        setContent {
            val dynamicColor by DataStoreManager.dynamicColorFlow.collectAsState(initial = Constants.PREF_DYNAMIC_COLOR_DEFAULT)
            val paletteStyle by DataStoreManager.paletteStyleFlow.collectAsState(initial = Constants.PREF_PALETTE_STYLE_DEFAULT)
            val contrastLevel by DataStoreManager.contrastLevelFlow.collectAsState(initial = Constants.PREF_CONTRAST_LEVEL_DEFAULT)
            val darkMode by DataStoreManager.darkModeFlow.collectAsState(initial = Constants.PREF_DARK_MODE_DEFAULT)
            val hapticFeedback by DataStoreManager.hapticFeedbackFlow.collectAsState(initial = Constants.PREF_HAPTIC_FEEDBACK_DEFAULT)

            val darkTheme = when (DarkMode.fromId(darkMode)) {
                DarkMode.FollowSystem -> isSystemInDarkTheme()
                DarkMode.Light -> false
                DarkMode.Dark -> true
            }

            LaunchedEffect(darkMode) {
                WindowCompat.getInsetsController(window, window.decorView).apply {
                    isAppearanceLightStatusBars = !darkTheme
                    isAppearanceLightNavigationBars = !darkTheme
                }
            }

            LaunchedEffect(hapticFeedback) {
                VibrationUtil.setEnabled(hapticFeedback)
            }

            MomoQRTheme(
                darkTheme = darkTheme,
                style = PaletteStyle.fromId(paletteStyle),
                contrastLevel = contrastLevel.toDouble(),
                dynamicColor = dynamicColor
            ) {
                CrashPage(
                    crashLog = crashLogs ?: stringResource(R.string.tip_no_crash_logs),
                    exitApp = ::finishAffinity,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}