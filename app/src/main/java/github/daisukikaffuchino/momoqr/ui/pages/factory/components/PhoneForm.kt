package github.daisukikaffuchino.momoqr.ui.pages.factory.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.ui.pages.factory.FIELD_PHONE_NUMBER

@Composable
fun PhoneForm(
    phoneNumber: String,
    onPhoneNumberChange: (String) -> Unit,
    invalidFields: Set<String>,
    shouldShowErrors: Boolean,
) {
    FactoryTextField(
        value = phoneNumber,
        onValueChange = onPhoneNumberChange,
        label = stringResource(R.string.label_factory_phone_number),
        supportingText = stringResource(R.string.tip_factory_message_phone),
        isError = shouldShowErrors && FIELD_PHONE_NUMBER in invalidFields,
        keyboardType = KeyboardType.Phone
    )
}
