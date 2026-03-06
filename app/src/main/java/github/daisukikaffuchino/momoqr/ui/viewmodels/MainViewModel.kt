package github.daisukikaffuchino.momoqr.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.navigation3.runtime.NavKey
import dagger.hilt.android.lifecycle.HiltViewModel
import github.daisukikaffuchino.momoqr.ui.navigation.MomoScreen
import github.daisukikaffuchino.momoqr.ui.navigation.TopLevelBackStack
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    val mainBackStack = TopLevelBackStack<NavKey>(MomoScreen.Home)
}