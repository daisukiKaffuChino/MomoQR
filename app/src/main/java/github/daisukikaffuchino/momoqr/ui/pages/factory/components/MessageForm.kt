package github.daisukikaffuchino.momoqr.ui.pages.factory.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.ui.pages.factory.FIELD_MESSAGE_BODY
import github.daisukikaffuchino.momoqr.ui.pages.factory.FIELD_MESSAGE_PHONE

@Composable
fun MessageForm(
    phoneNumber: String,
    onPhoneNumberChange: (String) -> Unit,
    body: String,
    onBodyChange: (String) -> Unit,
    invalidFields: Set<String>,
    shouldShowErrors: Boolean,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        FactoryTextField(
            value = phoneNumber,
            onValueChange = onPhoneNumberChange,
            label = stringResource(R.string.label_factory_message_phone),
            supportingText = stringResource(R.string.tip_factory_message_phone),
            isError = shouldShowErrors && FIELD_MESSAGE_PHONE in invalidFields,
            keyboardType = KeyboardType.Phone
        )

        FactoryTextField(
            value = body,
            onValueChange = onBodyChange,
            label = stringResource(R.string.label_factory_message_body),
            supportingText = stringResource(R.string.tip_factory_message_body),
            isError = shouldShowErrors && FIELD_MESSAGE_BODY in invalidFields,
            minLines = 4
        )
    }
}
