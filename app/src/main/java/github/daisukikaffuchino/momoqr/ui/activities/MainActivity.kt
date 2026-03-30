package github.daisukikaffuchino.momoqr.ui.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldState
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldValue
import androidx.compose.material3.adaptive.navigationsuite.rememberNavigationSuiteScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation3.runtime.NavKey
import dagger.hilt.android.AndroidEntryPoint
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.constants.AppConstants
import github.daisukikaffuchino.momoqr.logic.datastore.DataStoreManager
import github.daisukikaffuchino.momoqr.logic.model.DarkMode
import github.daisukikaffuchino.momoqr.logic.model.AppPaletteStyle
import github.daisukikaffuchino.momoqr.logic.model.ThemeAccentColor
import github.daisukikaffuchino.momoqr.ui.navigation.MomoDestination
import github.daisukikaffuchino.momoqr.ui.navigation.TopLevelBackStack
import github.daisukikaffuchino.momoqr.ui.navigation.TopNavigation
import github.daisukikaffuchino.momoqr.ui.theme.Defaults
import github.daisukikaffuchino.momoqr.ui.theme.MomoQRTheme
import github.daisukikaffuchino.momoqr.ui.viewmodels.MainViewModel
import github.daisukikaffuchino.momoqr.utils.VibrationUtil
import github.daisukikaffuchino.momoqr.utils.configureEdgeToEdge
import github.daisukikaffuchino.momoqr.utils.setAppLanguage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var mainBackStack: TopLevelBackStack<NavKey>
    lateinit var mainViewModel: MainViewModel
    lateinit var navigationScaffoldState: NavigationSuiteScaffoldState
    private var exitConfirmation = AppConstants.PREF_EXIT_CONFIRMATION_DEFAULT
    private var lastBackPressTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        configureEdgeToEdge()

        runBlocking {
            val dataStoreManager = DataStoreManager
            val code = dataStoreManager.languageFlow.first()
            setAppLanguage(code)
        }

        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                DataStoreManager.exitConfirmationFlow.collect { confirmation ->
                    exitConfirmation = confirmation
                }
            }
        }

        onBackPressedDispatcher.addCallback(this) {
            if (mainBackStack.backStack.size <= 1) {
                if (exitConfirmation) {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastBackPressTime > 1500) {
                        lastBackPressTime = currentTime
                        Toast.makeText(
                            this@MainActivity,
                            R.string.toast_press_again_to_exit,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else finish()
                } else finish()
            } else mainBackStack.removeLast()
        }

        setContent {
            mainViewModel = hiltViewModel<MainViewModel>()
            mainBackStack = mainViewModel.mainBackStack
            navigationScaffoldState = rememberNavigationSuiteScaffoldState()

            // 主题
            val dynamicColor by DataStoreManager.dynamicColorFlow.collectAsState(initial = AppConstants.PREF_DYNAMIC_COLOR_DEFAULT)
            val accentColor by DataStoreManager.accentColorFlow.collectAsState(initial = ThemeAccentColor.PINK)
            val paletteStyle by DataStoreManager.paletteStyleFlow.collectAsState(initial = AppConstants.PREF_PALETTE_STYLE_DEFAULT)
            val contrastLevel by DataStoreManager.contrastLevelFlow.collectAsState(initial = AppConstants.PREF_CONTRAST_LEVEL_DEFAULT)
            val darkMode by DataStoreManager.darkModeFlow.collectAsState(initial = AppConstants.PREF_DARK_MODE_DEFAULT)
            val hapticFeedback by DataStoreManager.hapticFeedbackFlow.collectAsState(initial = AppConstants.PREF_HAPTIC_FEEDBACK_DEFAULT)

            // 深色模式
            val darkTheme = when (DarkMode.fromId(darkMode)) {
                DarkMode.FollowSystem -> isSystemInDarkTheme()
                DarkMode.Light -> false
                DarkMode.Dark -> true
            }
            // 配置状态栏和底部导航栏的颜色（在用户切换深色模式时）
            // https://github.com/dn0ne/lotus/blob/master/app/src/main/java/com/dn0ne/player/MainActivity.kt#L266
            LaunchedEffect(darkMode) {
                WindowCompat.getInsetsController(window, window.decorView).apply {
                    isAppearanceLightStatusBars = !darkTheme
                    isAppearanceLightNavigationBars = !darkTheme
                }
            }

            LaunchedEffect(hapticFeedback) {
                VibrationUtil.setEnabled(hapticFeedback)
            }

            // 当BackStack出现非顶层路由时，隐藏底部导航栏
            LaunchedEffect(mainBackStack.backStack.lastOrNull()) {
                val isTopLevel =
                    mainBackStack.backStack.lastOrNull() in MomoDestination.entries.map { it.route }
                if (isTopLevel) {
                    if (navigationScaffoldState.currentValue != NavigationSuiteScaffoldValue.Visible) navigationScaffoldState.show()
                } else {
                    if (navigationScaffoldState.currentValue != NavigationSuiteScaffoldValue.Hidden) navigationScaffoldState.hide()
                }
            }

            MomoQRTheme(
                customKeyColor = accentColor.colors[0],
                darkTheme = darkTheme,
                style = AppPaletteStyle.fromId(paletteStyle),
                contrastLevel = contrastLevel.toDouble(),
                dynamicColor = dynamicColor
            ) {
                MomoQRApp()
            }

        }
    }

    @Composable
    fun MomoQRApp() {
        Surface(
            color = Defaults.Colors.Background,
            modifier = Modifier.fillMaxSize()
        ) {
            val view = LocalView.current
            NavigationSuiteScaffold(
                state = navigationScaffoldState,
                navigationSuiteItems = {
                    MomoDestination.entries.forEach { destination ->
                        val selected = destination.route == mainBackStack.topLevelKey
                        item(
                            icon = {
                                Crossfade(selected) {
                                    if (it) Icon(
                                        painter = painterResource(destination.selectedIcon),
                                        contentDescription = null
                                    )
                                    else Icon(
                                        painter = painterResource(destination.icon),
                                        contentDescription = null
                                    )
                                }
                            },
                            label = { Text(stringResource(destination.label)) },
                            selected = selected,
                            onClick = {
                                VibrationUtil.performHapticFeedback(view)
                                mainBackStack.addTopLevel(destination.route)
                            }
                        )
                    }
                },
                containerColor = Defaults.Colors.Background,
                modifier = Modifier.fillMaxSize()
            ) {
                TopNavigation(
                    backStack = mainBackStack,
                    viewModel = mainViewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }


}


