package github.daisukikaffuchino.momoqr.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import github.daisukikaffuchino.momoqr.ui.theme.Defaults

@Composable
fun ListItemContainer(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    content: LazyListScope.() -> Unit,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(Defaults.settingsItemPadding),
        state = state,
        modifier = modifier
    ) {
        item {
            Spacer(modifier = Modifier.width(Defaults.screenVerticalPadding))
        }

        content()

        item {
            Spacer(modifier = Modifier.width(Defaults.screenVerticalPadding))
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
fun LazyListScope.segmentedGroup(
    content: @Composable ColumnScope.() -> Unit
) {
    item {
        Column(
            verticalArrangement = Arrangement.spacedBy(Defaults.settingsSegmentedItemPadding),
            modifier = Modifier.clip(MaterialTheme.shapes.largeIncreased)
        ) {
            content()
        }
    }

    item {
        Spacer(modifier = Modifier.size(Defaults.settingsItemPadding))
    }
}


fun LazyListScope.segmentedSection(
    titleRes: Int? = null,
    titleString: String? = null,
    content: LazyListScope.() -> Unit
) {
    if (titleRes != null || titleString != null) {
        item {
            val titleText = titleRes?.let { stringResource(it) } ?: titleString.orEmpty()

            Text(
                text = titleText,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(
                    horizontal = Defaults.screenVerticalPadding,
                    vertical = Defaults.settingsItemPadding
                )
            )
        }
    }

    content()

    item {
        Spacer(modifier = Modifier.size(Defaults.settingsItemPadding))
    }
}