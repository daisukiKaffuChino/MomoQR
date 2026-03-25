package github.daisukikaffuchino.momoqr.ui.pages.factory.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.logic.model.WifiSecurity
import github.daisukikaffuchino.momoqr.ui.components.TextCheckbox
import github.daisukikaffuchino.momoqr.ui.pages.factory.FIELD_WIFI_PASSWORD
import github.daisukikaffuchino.momoqr.ui.pages.factory.FIELD_WIFI_SSID

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
    Column(
        modifier = Modifier.animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FactoryTextField(
            value = ssid,
            onValueChange = onSsidChange,
            label = stringResource(R.string.label_factory_wifi_name),
            isError = shouldShowErrors && FIELD_WIFI_SSID in invalidFields
        )

        Text(
            modifier = Modifier.padding(8.dp),
            text = stringResource(R.string.label_factory_wifi_security),
            style = MaterialTheme.typography.titleSmall
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            WifiSecurity.entries.forEach { item ->
                FilterChip(
                    modifier = Modifier
                        .height(36.dp),
                    selected = item == security,
                    onClick = { onSecurityChange(item) },
                    label = { Text(text = stringResource(item.labelRes)) }
                )
            }
        }
        AnimatedVisibility(
            visible = security !=WifiSecurity.None,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            FactoryTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = stringResource(R.string.label_factory_wifi_password),
                supportingText = stringResource(R.string.tip_factory_wifi_password_requirement),
                isError = shouldShowErrors && FIELD_WIFI_PASSWORD in invalidFields
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
