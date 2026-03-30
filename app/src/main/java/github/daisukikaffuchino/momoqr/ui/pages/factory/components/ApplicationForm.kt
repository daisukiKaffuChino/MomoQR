package github.daisukikaffuchino.momoqr.ui.pages.factory.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.ui.components.CustomTextField
import github.daisukikaffuchino.momoqr.ui.pages.factory.FIELD_APP_PACKAGE

@Composable
fun ApplicationForm(
    packageName: String,
    onPackageNameChange: (String) -> Unit,
    invalidFields: Set<String>,
    shouldShowErrors: Boolean,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        CustomTextField(
            value = packageName,
            onValueChange = onPackageNameChange,
            label = stringResource(R.string.label_factory_app_package),
            supportingText = stringResource(R.string.tip_factory_app_package),
            isError = shouldShowErrors && FIELD_APP_PACKAGE in invalidFields
        )
    }
}
