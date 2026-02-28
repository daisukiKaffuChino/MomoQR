package github.daisukikaffuchino.momoqr.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.navigation3.runtime.NavKey
import github.daisukikaffuchino.momoqr.ui.navigation.MomoScreen
import github.daisukikaffuchino.momoqr.ui.navigation.TopLevelBackStack
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel : ViewModel() {
    val mainBackStack = TopLevelBackStack<NavKey>(MomoScreen.Home)
}