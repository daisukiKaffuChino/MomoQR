package github.daisukikaffuchino.momoqr.ui.pages.factory.components

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.ui.components.TextCheckbox
import github.daisukikaffuchino.momoqr.ui.pages.factory.FIELD_WIFI_PASSWORD
import github.daisukikaffuchino.momoqr.ui.pages.factory.FIELD_WIFI_SSID
import github.daisukikaffuchino.momoqr.ui.pages.factory.WifiSecurity

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WifiForm(
    ssid: String,
    onSsidChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    security: WifiSecurity,
    onSecurityChange: (WifiSecurity) -> Unit,
    hidden: Boolean,
    onHiddenChange: (Boolean) -> Unit,
    invalidFields: Set<String>,
    shouldShowErrors: Boolean,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        FactoryTextField(
            value = ssid,
            onValueChange = onSsidChange,
            label = stringResource(R.string.label_factory_wifi_name),
            isError = shouldShowErrors && FIELD_WIFI_SSID in invalidFields
        )

        Text(
            text = stringResource(R.string.label_factory_wifi_security),
            style = MaterialTheme.typography.titleSmall
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            WifiSecurity.values().forEach { item ->
                FilterChip(
                    selected = item == security,
                    onClick = { onSecurityChange(item) },
                    label = { Text(text = stringResource(item.labelRes)) }
                )
            }
        }

        if (security != WifiSecurity.None) {
            FactoryTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = stringResource(R.string.label_factory_wifi_password),
                supportingText = stringResource(R.string.tip_factory_wifi_password_requirement),
                isError = shouldShowErrors && FIELD_WIFI_PASSWORD in invalidFields,
                keyboardType = KeyboardType.Password
            )
        }

        TextCheckbox(
            checked = hidden,
            onCheckedChange = onHiddenChange,
            stringRes = R.string.label_factory_wifi_hidden,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
