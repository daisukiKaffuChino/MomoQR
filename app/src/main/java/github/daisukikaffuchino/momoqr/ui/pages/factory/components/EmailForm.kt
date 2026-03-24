package github.daisukikaffuchino.momoqr.ui.pages.factory.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.ui.pages.factory.FIELD_EMAIL_ADDRESS

@Composable
fun EmailForm(
    emailAddress: String,
    onEmailAddressChange: (String) -> Unit,
    subject: String,
    onSubjectChange: (String) -> Unit,
    body: String,
    onBodyChange: (String) -> Unit,
    invalidFields: Set<String>,
    shouldShowErrors: Boolean,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        FactoryTextField(
            value = emailAddress,
            onValueChange = onEmailAddressChange,
            label = stringResource(R.string.label_factory_email_address),
            isError = shouldShowErrors && FIELD_EMAIL_ADDRESS in invalidFields,
            keyboardType = KeyboardType.Email
        )

        FactoryTextField(
            value = subject,
            onValueChange = onSubjectChange,
            label = stringResource(R.string.label_factory_email_subject)
        )

        FactoryTextField(
            value = body,
            onValueChange = onBodyChange,
            label = stringResource(R.string.label_factory_email_body),
            minLines = 4
        )
    }
}
