package github.daisukikaffuchino.momoqr.ui.pages.factory.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import github.daisukikaffuchino.momoqr.logic.model.FactoryType
import github.daisukikaffuchino.momoqr.ui.pages.factory.FactoryViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TypeChipsCard(
    selectedType: FactoryType,
    viewModel: FactoryViewModel
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.largeIncreased,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(
                space = 8.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FactoryType.entries.forEach { type ->
                val selected = type == selectedType
                FilterChip(
                    modifier = Modifier
                        .height(36.dp),
                    border = null,
                    shape = RoundedCornerShape(14.dp),
                    selected = selected,
                    onClick = { viewModel.updateSelectedType(type) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    leadingIcon = {
                        Icon(
                            modifier = Modifier.size(FilterChipDefaults.IconSize),
                            painter = painterResource(type.iconRes),
                            contentDescription = null,
                            tint = if (selected) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(type.labelRes),
                            style = MaterialTheme.typography.titleSmall,
                            maxLines = 1
                        )
                    }
                )
            }
        }
    }
}