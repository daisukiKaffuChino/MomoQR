package github.daisukikaffuchino.momoqr.ui.pages.palette.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.ui.theme.Defaults

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PalettePagerSwitcher(
    currentPage: Int,
    onPageSelected: (Int) -> Unit,
) {
    val labels = listOf(
        stringResource(R.string.action_palette_edit),
        stringResource(R.string.action_palette_presets),
    )
    ButtonGroup(
        overflowIndicator = { menuState ->
            ButtonGroupDefaults.OverflowIndicator(menuState = menuState)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = Defaults.screenHorizontalPadding
            )
            .padding(bottom = 8.dp)
    ) {
        labels.forEachIndexed { index, label ->
            toggleableItem(
                checked = currentPage == index,
                label = label,
                onCheckedChange = { checked ->
                    if (checked) {
                        onPageSelected(index)
                    }
                },
                weight = 1f,
            )
        }
    }
}
