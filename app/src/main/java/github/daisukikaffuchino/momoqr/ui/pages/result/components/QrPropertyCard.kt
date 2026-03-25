package github.daisukikaffuchino.momoqr.ui.pages.result.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.utils.toLocalDateString

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun QrPropertyCard(
    modifiedTime: Long,
    createdTime: Long,
    errorCorrectionLevel: String,
    modifier: Modifier = Modifier
) {
    val dateText = if (modifiedTime == 0L)
        stringResource(R.string.none)
    else
        modifiedTime.toLocalDateString()

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = stringResource(R.string.label_attributes),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            if (createdTime != 0L) {
                PropertyItem(
                    label = stringResource(R.string.label_creation_date),
                    value = createdTime.toLocalDateString()
                )
            }

            PropertyItem(
                label = stringResource(R.string.label_modification_date),
                value = dateText
            )

            PropertyItem(
                label = stringResource(R.string.pref_error_correction_level),
                value = errorCorrectionLevel
            )
        }
    }

    Spacer(modifier = Modifier.size(24.dp))

}

@Composable
private fun PropertyItem(
    label: String,
    value: String
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}