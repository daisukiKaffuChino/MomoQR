package github.daisukikaffuchino.momoqr.ui.pages.palette

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.ui.components.TopAppBarScaffold

@Composable
fun PalettePage(
    modifier: Modifier = Modifier,
    onNavigateUp: () -> Unit,
) {

    TopAppBarScaffold(
        title = stringResource(R.string.label_generate_color_palette),
        onBack = onNavigateUp,
        modifier = modifier
    ) {

    }
}