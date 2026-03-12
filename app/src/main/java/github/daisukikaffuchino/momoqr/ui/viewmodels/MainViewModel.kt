package github.daisukikaffuchino.momoqr.ui.viewmodels

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavKey
import dagger.hilt.android.lifecycle.HiltViewModel
import github.daisukikaffuchino.momoqr.logic.database.Repository
import github.daisukikaffuchino.momoqr.logic.database.StarEntity
import github.daisukikaffuchino.momoqr.logic.datastore.DataStoreManager
import github.daisukikaffuchino.momoqr.ui.navigation.MomoScreen
import github.daisukikaffuchino.momoqr.ui.navigation.TopLevelBackStack
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import github.daisukikaffuchino.momoqr.logic.model.SortingMethod
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel @Inject constructor() : ViewModel() {
    val mainBackStack = TopLevelBackStack<NavKey>(MomoScreen.Home)

    private val starList: Flow<List<StarEntity>> = Repository.getAllStars()

    val sortedStarList: StateFlow<List<StarEntity>> = DataStoreManager.sortingMethodFlow
        .flatMapLatest { sortingMethod ->
            starList.map { list ->
                when (SortingMethod.fromId(sortingMethod)) {
                    SortingMethod.Sequential -> list.sortedWith(
                        compareByDescending<StarEntity> { it.marked }
                            .thenBy { it.id }
                    )

                    SortingMethod.Category -> list.sortedWith(
                        compareByDescending<StarEntity> { it.marked }
                            .thenBy { it.category }
                    )

                    SortingMethod.AlphabeticalAscending -> list.sortedWith(
                        compareByDescending<StarEntity> { it.marked }
                            .thenBy { it.content }
                    )

                    SortingMethod.AlphabeticalDescending -> list.sortedWith(
                        compareByDescending<StarEntity> { it.marked }
                            .thenByDescending { it.content }
                    )
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val starListState = LazyListState()
    var searchMode by mutableStateOf(false)
        private set
    val searchFieldState = TextFieldState()

    private val _selectedStarIds = MutableStateFlow(listOf<Int>())
    val selectedStarIds = _selectedStarIds.asStateFlow()

    fun addStar(star: StarEntity) {
        viewModelScope.launch {
            Repository.insertStar(star)
        }
    }

//    fun updateStar(star: StarEntity) {
//        viewModelScope.launch {
//            Repository.updateStar(star)
//        }
//    }

    fun deleteStar(star: StarEntity) {
        viewModelScope.launch {
            Repository.deleteStar(star)
        }
    }

    fun toggleStarSelection(star: StarEntity) {
        _selectedStarIds.update { idList ->
            if (idList.contains(star.id))
                idList - star.id
            else
                idList + star.id
        }
    }

    fun selectAllStars() {
        viewModelScope.launch {
            starList.firstOrNull()?.let { stars ->
                val allIds = stars.map { it.id }
                _selectedStarIds.value = allIds
            }
        }
    }

    fun isAllSelected(): Boolean {
        return sortedStarList.value.isNotEmpty() &&
                selectedStarIds.value.size == sortedStarList.value.size
    }

    fun clearAllStarsSelection() {
        _selectedStarIds.update { emptyList() }
    }

    fun deleteSelectedStar() {
        viewModelScope.launch {
            Repository.deleteStarFromIds(selectedStarIds.value)
            clearAllStarsSelection()
        }
    }

    fun setSearchModeEnabled(enabled: Boolean) {
        searchMode = enabled
    }

}