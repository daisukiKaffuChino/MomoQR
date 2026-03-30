package github.daisukikaffuchino.momoqr.ui.activities

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import github.daisukikaffuchino.momoqr.constants.AppConstants
import github.daisukikaffuchino.momoqr.logic.datastore.DataStoreManager
import github.daisukikaffuchino.momoqr.logic.model.AppPaletteStyle
import github.daisukikaffuchino.momoqr.logic.model.DarkMode
import github.daisukikaffuchino.momoqr.logic.model.ThemeAccentColor
import github.daisukikaffuchino.momoqr.ui.theme.MomoQRTheme
import github.daisukikaffuchino.momoqr.utils.VibrationUtil
import github.daisukikaffuchino.momoqr.utils.configureEdgeToEdge

abstract class BaseActivity : AppCompatActivity() {

    /**
     * 是否在 super.onCreate() 前安装 SplashScreen
     */
    protected open fun shouldInstallSplashScreen(): Boolean = false

    /**
     * 是否在 super.onCreate() 前执行一些初始化逻辑
     * 例如设置语言
     */
    protected open fun beforeSuperOnCreate() = Unit

    /**
     * 子类自己的 onCreate 额外逻辑
     */
    protected open fun onActivityCreated(savedInstanceState: Bundle?) = Unit

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        if (shouldInstallSplashScreen()) {
            installSplashIfNeeded()
        }

        configureEdgeToEdge()
        beforeSuperOnCreate()
        super.onCreate(savedInstanceState)
        onActivityCreated(savedInstanceState)

        setContent {
            val dynamicColor by DataStoreManager.dynamicColorFlow.collectAsState(
                initial = AppConstants.PREF_DYNAMIC_COLOR_DEFAULT
            )
            val accentColor by DataStoreManager.accentColorFlow.collectAsState(
                initial = ThemeAccentColor.PINK
            )
            val paletteStyle by DataStoreManager.paletteStyleFlow.collectAsState(
                initial = AppConstants.PREF_PALETTE_STYLE_DEFAULT
            )
            val contrastLevel by DataStoreManager.contrastLevelFlow.collectAsState(
                initial = AppConstants.PREF_CONTRAST_LEVEL_DEFAULT
            )
            val darkMode by DataStoreManager.darkModeFlow.collectAsState(
                initial = AppConstants.PREF_DARK_MODE_DEFAULT
            )
            val hapticFeedback by DataStoreManager.hapticFeedbackFlow.collectAsState(
                initial = AppConstants.PREF_HAPTIC_FEEDBACK_DEFAULT
            )

            val darkTheme = when (DarkMode.fromId(darkMode)) {
                DarkMode.FollowSystem -> isSystemInDarkTheme()
                DarkMode.Light -> false
                DarkMode.Dark -> true
            }

            LaunchedEffect(darkTheme) {
                WindowCompat.getInsetsController(window, window.decorView).apply {
                    isAppearanceLightStatusBars = !darkTheme
                    isAppearanceLightNavigationBars = !darkTheme
                }
            }

            LaunchedEffect(hapticFeedback) {
                VibrationUtil.setEnabled(hapticFeedback)
            }

            MomoQRTheme(
                customKeyColor = accentColor.colors[0],
                darkTheme = darkTheme,
                style = AppPaletteStyle.fromId(paletteStyle),
                contrastLevel = contrastLevel.toDouble(),
                dynamicColor = dynamicColor
            ) {
                ActivityContent()
            }
        }
    }

    /**
     * 子类真正的 Compose 内容
     */
    @Composable
    protected abstract fun ActivityContent()

    /**
     * 单独抽出来，避免 BaseActivity 直接依赖具体实现细节
     */
    protected open fun installSplashIfNeeded() = Unit
}