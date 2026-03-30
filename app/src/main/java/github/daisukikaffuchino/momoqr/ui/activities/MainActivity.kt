package github.daisukikaffuchino.momoqr.ui.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.addCallback
import androidx.compose.animation.Crossfade
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation3.runtime.NavKey
import dagger.hilt.android.AndroidEntryPoint
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.constants.AppConstants
import github.daisukikaffuchino.momoqr.logic.datastore.DataStoreManager
import github.daisukikaffuchino.momoqr.ui.navigation.MomoDestination
import github.daisukikaffuchino.momoqr.ui.navigation.TopLevelBackStack
import github.daisukikaffuchino.momoqr.ui.navigation.TopNavigation
import github.daisukikaffuchino.momoqr.ui.theme.Defaults
import github.daisukikaffuchino.momoqr.ui.viewmodels.MainViewModel
import github.daisukikaffuchino.momoqr.utils.VibrationUtil
import github.daisukikaffuchino.momoqr.utils.setAppLanguage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    lateinit var mainBackStack: TopLevelBackStack<NavKey>
    lateinit var mainViewModel: MainViewModel
    lateinit var navigationScaffoldState: NavigationSuiteScaffoldState

    private var exitConfirmation = AppConstants.PREF_EXIT_CONFIRMATION_DEFAULT
    private var lastBackPressTime = 0L

    override fun shouldInstallSplashScreen(): Boolean = true

    override fun installSplashIfNeeded() {
        installSplashScreen()
    }

    override fun beforeSuperOnCreate() {
        runBlocking {
            val code = DataStoreManager.languageFlow.first()
            setAppLanguage(code)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
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
                    } else {
                        finish()
                    }
                } else {
                    finish()
                }
            } else {
                mainBackStack.removeLast()
            }
        }
    }

    @Composable
    override fun ActivityContent() {
        mainViewModel = hiltViewModel<MainViewModel>()
        mainBackStack = mainViewModel.mainBackStack
        navigationScaffoldState = rememberNavigationSuiteScaffoldState()

        LaunchedEffect(mainBackStack.backStack.lastOrNull()) {
            val isTopLevel =
                mainBackStack.backStack.lastOrNull() in MomoDestination.entries.map { it.route }

            if (isTopLevel) {
                if (navigationScaffoldState.currentValue != NavigationSuiteScaffoldValue.Visible)
                    navigationScaffoldState.show()
            } else {
                if (navigationScaffoldState.currentValue != NavigationSuiteScaffoldValue.Hidden)
                    navigationScaffoldState.hide()
            }
        }

        MomoQRApp()
    }

    @Composable
    private fun MomoQRApp() {
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
                                Crossfade(selected) { isSelected ->
                                    if (isSelected)
                                        Icon(
                                            painter = painterResource(destination.selectedIcon),
                                            contentDescription = null
                                        )
                                    else
                                        Icon(
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