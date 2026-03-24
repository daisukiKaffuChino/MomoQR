package github.daisukikaffuchino.momoqr.ui.pages.factory.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import github.daisukikaffuchino.momoqr.logic.model.FactoryType
import github.daisukikaffuchino.momoqr.ui.pages.factory.FactoryViewModel

@Composable
fun TypeChipsCard(
    selectedType: FactoryType,
    viewModel: FactoryViewModel
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(
                space = 12.dp,
                alignment = androidx.compose.ui.Alignment.CenterHorizontally
            ),
            verticalArrangement = Arrangement.spacedBy(0.dp),
            maxItemsInEachRow = 3
        ) {
            FactoryType.entries.forEach { type ->
                val selected = type == selectedType
                FilterChip(
                    selected = selected,
                    onClick = { viewModel.updateSelectedType(type) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color.Transparent,
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(type.iconRes),
                            contentDescription = null,
                            tint = if (selected) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    },
                    label = { Text(text = stringResource(type.labelRes)) }
                )
            }
        }
    }
}