package github.daisukikaffuchino.momoqr.ui.pages.result

import android.annotation.SuppressLint
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import github.daisukikaffuchino.momoqr.constants.Constants
import github.daisukikaffuchino.momoqr.logic.database.StarEntity

@SuppressLint("ComposableNaming")
@Composable
fun ResultAddPage(
    stars: StarEntity,
    onSave: (StarEntity) -> Unit,
    onNavigateUp: () -> Unit
) = ResultEditorPage(
    stars = stars,
    onSave = onSave,
    onDelete = {},
    onNavigateUp = onNavigateUp
)

@Composable
fun SharedTransitionScope.ResultEditPage(
    modifier: Modifier = Modifier,
    stars: StarEntity,
    onSave: (StarEntity) -> Unit,
    onDelete: () -> Unit,
    onNavigateUp: () -> Unit
) = ResultEditorPage(
    stars = stars,
    modifier = modifier.sharedBounds(
        sharedContentState = rememberSharedContentState(key = "${Constants.KEY_STARS_ITEM_TRANSITION}_${stars.id}"),
        animatedVisibilityScope = LocalNavAnimatedContentScope.current,
        resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
    ),
    onSave = onSave,
    onDelete = onDelete,
    onNavigateUp = onNavigateUp
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ResultEditorPage(
    modifier: Modifier = Modifier,
    stars: StarEntity? = null,
    onSave: (StarEntity) -> Unit,
    onDelete: () -> Unit,
    onNavigateUp: () -> Unit
) {
    Text(stars?.content ?: String())
}