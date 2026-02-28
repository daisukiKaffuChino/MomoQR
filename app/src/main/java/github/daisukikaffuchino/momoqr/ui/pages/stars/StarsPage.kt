package github.daisukikaffuchino.momoqr.ui.pages.stars

import androidx.compose.animation.SharedTransitionScope
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import github.daisukikaffuchino.momoqr.logic.database.StarEntity
import github.daisukikaffuchino.momoqr.ui.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SharedTransitionScope.StarsPage(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    //toTodoAddPage: () -> Unit,
    resultPage: (StarEntity) -> Unit,
) {

}