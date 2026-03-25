package github.daisukikaffuchino.momoqr.ui.components

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.utils.VibrationUtil

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterChipGroup(
    modifier: Modifier = Modifier,
    items: List<ChipItem>,
    defaultSelectedItemIndex: Int = 0,
    onSelectedChanged: (Int) -> Unit = {}
) {
    val view = LocalView.current
    var selectedItemIndex by rememberSaveable { mutableIntStateOf(defaultSelectedItemIndex) }

    LaunchedEffect(defaultSelectedItemIndex) {
        selectedItemIndex = defaultSelectedItemIndex
    }

    FlowRow(modifier = modifier) {
        items.forEach { item ->
            val selected = selectedItemIndex == item.id
            FilterChip(
                modifier = Modifier
                    .padding(end = 8.dp),
                selected = selected,
                border = null,
                shape = RoundedCornerShape(12.dp),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    selectedContainerColor = MaterialTheme.colorScheme.secondary,
                    selectedLabelColor = MaterialTheme.colorScheme.onSecondary,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onSecondary
                ),
                onClick = {
                    selectedItemIndex = item.id
                    VibrationUtil.performHapticFeedback(view)
                    onSelectedChanged(item.id)
                },
                label = {
                    Text(
                        text = item.name,
                        maxLines = 1
                    )
                },
                leadingIcon =
                    if (selected) {
                        {
                            Icon(
                                painter = painterResource(R.drawable.ic_check),
                                contentDescription = stringResource(R.string.tip_selected),
                                tint = MaterialTheme.colorScheme.onSecondary,
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    } else null
            )
        }
    }
}

data class ChipItem(
    val id: Int,
    val name: String
)