package github.daisukikaffuchino.momoqr.ui.pages.factory

import android.annotation.SuppressLint
import android.net.Uri
import android.telephony.PhoneNumberUtils
import android.util.Patterns
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.logic.database.StarEntity
import github.daisukikaffuchino.momoqr.ui.components.TopAppBarScaffold
import github.daisukikaffuchino.momoqr.ui.pages.factory.components.ApplicationForm
import github.daisukikaffuchino.momoqr.ui.pages.factory.components.EmailForm
import github.daisukikaffuchino.momoqr.ui.pages.factory.components.EventForm
import github.daisukikaffuchino.momoqr.ui.pages.factory.components.FactoryDateTimePickerDialogs
import github.daisukikaffuchino.momoqr.ui.pages.factory.components.GeoForm
import github.daisukikaffuchino.momoqr.ui.pages.factory.components.MessageForm
import github.daisukikaffuchino.momoqr.ui.pages.factory.components.PhoneForm
import github.daisukikaffuchino.momoqr.ui.pages.factory.components.WifiForm
import github.daisukikaffuchino.momoqr.ui.pages.result.components.ResultFloatingActionButton
import github.daisukikaffuchino.momoqr.ui.theme.Defaults
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun FactoryPage(
    modifier: Modifier = Modifier,
    onNavigateUp: () -> Unit,
    toResultAddPage: (StarEntity) -> Unit,
    viewModel: FactoryViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val selectedType = FactoryType.valueOf(state.selectedTypeName)
    val validation = state.validation

    TopAppBarScaffold(
        title = stringResource(R.string.label_generate_new),
        onBack = onNavigateUp,
        enableVerticalBounce = false,
        floatingActionButton = {
            ResultFloatingActionButton(
                text = stringResource(R.string.action_edit_result),
                iconRes = R.drawable.ic_edit_square,
                onClick = {
                    if (validation.content == null) {
                        viewModel.showErrors()
                        return@ResultFloatingActionButton
                    }
                    toResultAddPage(
                        StarEntity(
                            title = context.getString(selectedType.labelRes),
                            content = validation.content
                        )
                    )
                }
            )
        },
        modifier = modifier
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = Defaults.screenVerticalPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            item {
                Text(
                    text = stringResource(R.string.tip_factory_page_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(end = 4.dp)
                ) {
                    items(FactoryType.values().toList()) { type ->
                        FilterChip(
                            selected = type == selectedType,
                            onClick = { viewModel.updateSelectedType(type) },
                            label = { Text(text = stringResource(type.labelRes)) }
                        )
                    }
                }
            }

            item {
                AnimatedContent(
                    targetState = selectedType,
                    transitionSpec = {
                        val forward = targetState.ordinal > initialState.ordinal
                        val slideSpec = tween<IntOffset>(
                            durationMillis = 240,
                            easing = FastOutSlowInEasing
                        )
                        (slideInHorizontally(
                            animationSpec = slideSpec,
                            initialOffsetX = { if (forward) it / 18 else -it / 18 }
                        ) + fadeIn(
                            animationSpec = tween(
                                durationMillis = 220,
                                delayMillis = 40,
                                easing = FastOutSlowInEasing
                            )
                        ))
                            .togetherWith(
                                slideOutHorizontally(
                                    animationSpec = slideSpec,
                                    targetOffsetX = { if (forward) -it / 18 else it / 18 }
                                ) + fadeOut(
                                    animationSpec = tween(
                                        durationMillis = 120,
                                        easing = FastOutSlowInEasing
                                    )
                                )
                            )
                            .using(SizeTransform(clip = false))
                    },
                    label = "factory_type_content"
                ) { targetType ->
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = stringResource(targetType.descriptionRes),
                            style = MaterialTheme.typography.titleMedium
                        )

                        when (targetType) {
                            FactoryType.Wifi -> WifiForm(
                                ssid = state.wifiSsid,
                                onSsidChange = viewModel::updateWifiSsid,
                                password = state.wifiPassword,
                                onPasswordChange = viewModel::updateWifiPassword,
                                security = WifiSecurity.valueOf(state.wifiSecurityName),
                                onSecurityChange = viewModel::updateWifiSecurity,
                                hidden = state.wifiHidden,
                                onHiddenChange = viewModel::updateWifiHidden,
                                invalidFields = validation.invalidFields,
                                shouldShowErrors = state.shouldShowErrors
                            )

                            FactoryType.Email -> EmailForm(
                                emailAddress = state.emailAddress,
                                onEmailAddressChange = viewModel::updateEmailAddress,
                                subject = state.emailSubject,
                                onSubjectChange = viewModel::updateEmailSubject,
                                body = state.emailBody,
                                onBodyChange = viewModel::updateEmailBody,
                                invalidFields = validation.invalidFields,
                                shouldShowErrors = state.shouldShowErrors
                            )

                            FactoryType.Event -> EventForm(
                                title = state.eventTitle,
                                onTitleChange = viewModel::updateEventTitle,
                                allDay = state.eventAllDay,
                                onAllDayChange = viewModel::updateEventAllDay,
                                start = state.eventStart,
                                onStartClick = { viewModel.openEventDatePicker(EventDateTimeTarget.Start) },
                                end = state.eventEnd,
                                onEndClick = { viewModel.openEventDatePicker(EventDateTimeTarget.End) },
                                location = state.eventLocation,
                                onLocationChange = viewModel::updateEventLocation,
                                description = state.eventDescription,
                                onDescriptionChange = viewModel::updateEventDescription,
                                invalidFields = validation.invalidFields,
                                shouldShowErrors = state.shouldShowErrors
                            )

                            FactoryType.Phone -> PhoneForm(
                                phoneNumber = state.phoneNumber,
                                onPhoneNumberChange = viewModel::updatePhoneNumber,
                                invalidFields = validation.invalidFields,
                                shouldShowErrors = state.shouldShowErrors
                            )

                            FactoryType.Message -> MessageForm(
                                phoneNumber = state.messagePhone,
                                onPhoneNumberChange = viewModel::updateMessagePhone,
                                body = state.messageBody,
                                onBodyChange = viewModel::updateMessageBody,
                                invalidFields = validation.invalidFields,
                                shouldShowErrors = state.shouldShowErrors
                            )

                            FactoryType.Application -> ApplicationForm(
                                packageName = state.appPackageName,
                                onPackageNameChange = viewModel::updateAppPackageName,
                                invalidFields = validation.invalidFields,
                                shouldShowErrors = state.shouldShowErrors
                            )

                            FactoryType.Geo -> GeoForm(
                                latitude = state.geoLatitude,
                                onLatitudeChange = viewModel::updateGeoLatitude,
                                longitude = state.geoLongitude,
                                onLongitudeChange = viewModel::updateGeoLongitude,
                                altitude = state.geoAltitude,
                                onAltitudeChange = viewModel::updateGeoAltitude,
                                invalidFields = validation.invalidFields,
                                shouldShowErrors = state.shouldShowErrors
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

    FactoryDateTimePickerDialogs(
        state = state,
        onDismissDatePicker = viewModel::dismissEventDatePicker,
        onConfirmDatePicker = viewModel::confirmEventDateSelection,
        onDismissTimePicker = viewModel::dismissEventTimePicker,
        onConfirmTimePicker = viewModel::confirmEventTimeSelection,
    )
}

fun buildWifiPayload(
    ssid: String,
    password: String,
    security: WifiSecurity,
    hidden: Boolean,
): FactoryValidation {
    val invalidFields = mutableSetOf<String>()
    val normalizedSsid = ssid.trim()
    val normalizedPassword = password.trim()

    if (normalizedSsid.isEmpty()) invalidFields += FIELD_WIFI_SSID
    if (security != WifiSecurity.None && normalizedPassword.length < WIFI_PASSWORD_MIN_LENGTH) {
        invalidFields += FIELD_WIFI_PASSWORD
    }

    if (invalidFields.isNotEmpty()) {
        return FactoryValidation(invalidFields = invalidFields)
    }

    val content = buildString {
        append("WIFI:")
        append("T:${security.protocol};")
        append("S:${escapeWifiValue(normalizedSsid)};")
        if (security != WifiSecurity.None) {
            append("P:${escapeWifiValue(normalizedPassword)};")
        }
        if (hidden) append("H:true;")
        append(";")
    }
    return FactoryValidation(content = content)
}

fun buildEmailPayload(
    emailAddress: String,
    subject: String,
    body: String,
): FactoryValidation {
    val invalidFields = mutableSetOf<String>()
    val normalizedEmail = emailAddress.trim()

    if (normalizedEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(normalizedEmail).matches()) {
        invalidFields += FIELD_EMAIL_ADDRESS
    }

    if (invalidFields.isNotEmpty()) {
        return FactoryValidation(invalidFields = invalidFields)
    }

    val query = buildList {
        subject.trim().takeIf { it.isNotEmpty() }?.let { add("subject=${Uri.encode(it)}") }
        body.trim().takeIf { it.isNotEmpty() }?.let { add("body=${Uri.encode(it)}") }
    }.joinToString("&")

    val content = buildString {
        append("mailto:")
        append(normalizedEmail)
        if (query.isNotEmpty()) {
            append("?")
            append(query)
        }
    }
    return FactoryValidation(content = content)
}

fun buildEventPayload(
    title: String,
    allDay: Boolean,
    start: String,
    end: String,
    location: String,
    description: String,
): FactoryValidation {
    val invalidFields = mutableSetOf<String>()
    val normalizedTitle = title.trim()
    val normalizedStart = start.trim()
    val normalizedEnd = end.trim()

    if (normalizedTitle.isEmpty()) invalidFields += FIELD_EVENT_TITLE

    val content = if (allDay) {
        val startDate = parseEventAllDayDate(normalizedStart)
        if (startDate == null) invalidFields += FIELD_EVENT_START

        if (invalidFields.isNotEmpty()) {
            return FactoryValidation(invalidFields = invalidFields)
        }

        val formatter = SimpleDateFormat(EVENT_ALL_DAY_OUTPUT_PATTERN, Locale.US)
        val endCalendar = java.util.Calendar.getInstance().apply {
            time = startDate!!
            add(java.util.Calendar.DAY_OF_YEAR, 1)
        }

        buildString {
            appendLine("BEGIN:VEVENT")
            appendLine("SUMMARY:${escapeIcsText(normalizedTitle)}")
            appendLine("DTSTART;VALUE=DATE:${formatter.format(startDate)}")
            appendLine("DTEND;VALUE=DATE:${formatter.format(endCalendar.time)}")
            location.trim().takeIf { it.isNotEmpty() }?.let {
                appendLine("LOCATION:${escapeIcsText(it)}")
            }
            description.trim().takeIf { it.isNotEmpty() }?.let {
                appendLine("DESCRIPTION:${escapeIcsText(it)}")
            }
            append("END:VEVENT")
        }
    } else {
        val startDate = parseEventDate(normalizedStart)
        if (startDate == null) invalidFields += FIELD_EVENT_START

        val endDate = parseEventDate(normalizedEnd)
        if (endDate == null) invalidFields += FIELD_EVENT_END

        if (startDate != null && endDate != null && endDate.before(startDate)) {
            invalidFields += FIELD_EVENT_END
        }

        if (invalidFields.isNotEmpty()) {
            return FactoryValidation(invalidFields = invalidFields)
        }

        val formatter = SimpleDateFormat(EVENT_OUTPUT_PATTERN, Locale.US)
        buildString {
            appendLine("BEGIN:VEVENT")
            appendLine("SUMMARY:${escapeIcsText(normalizedTitle)}")
            appendLine("DTSTART:${formatter.format(startDate!!)}")
            appendLine("DTEND:${formatter.format(endDate!!)}")
            location.trim().takeIf { it.isNotEmpty() }?.let {
                appendLine("LOCATION:${escapeIcsText(it)}")
            }
            description.trim().takeIf { it.isNotEmpty() }?.let {
                appendLine("DESCRIPTION:${escapeIcsText(it)}")
            }
            append("END:VEVENT")
        }
    }
    return FactoryValidation(content = content)
}

fun buildPhonePayload(phoneNumber: String): FactoryValidation {
    val normalizedPhone = normalizeValidPhoneNumber(phoneNumber)
    if (normalizedPhone == null) {
        return FactoryValidation(invalidFields = setOf(FIELD_PHONE_NUMBER))
    }
    return FactoryValidation(content = "tel:$normalizedPhone")
}

fun buildMessagePayload(
    phoneNumber: String,
    body: String,
): FactoryValidation {
    val invalidFields = mutableSetOf<String>()
    val normalizedPhone = normalizeValidPhoneNumber(phoneNumber)
    val normalizedBody = body.trim()

    if (normalizedPhone == null) {
        invalidFields += FIELD_MESSAGE_PHONE
    }

    if (normalizedBody.isEmpty()) {
        invalidFields += FIELD_MESSAGE_BODY
    }

    if (invalidFields.isNotEmpty()) {
        return FactoryValidation(invalidFields = invalidFields)
    }

    return FactoryValidation(content = "SMSTO:$normalizedPhone:$normalizedBody")
}

fun buildApplicationPayload(
    packageName: String,
): FactoryValidation {
    val normalizedPackage = packageName.trim()
    val invalidFields = mutableSetOf<String>()

    if (normalizedPackage.isEmpty()) {
        invalidFields += FIELD_APP_PACKAGE
    }

    if (normalizedPackage.isNotEmpty() && !PACKAGE_NAME_REGEX.matches(normalizedPackage)) {
        invalidFields += FIELD_APP_PACKAGE
    }

    if (invalidFields.isNotEmpty()) {
        return FactoryValidation(invalidFields = invalidFields)
    }

    val content = "https://play.google.com/store/apps/details?id=${Uri.encode(normalizedPackage)}"

    return FactoryValidation(content = content)
}

fun buildGeoPayload(
    latitude: String,
    longitude: String,
    altitude: String,
): FactoryValidation {
    val invalidFields = mutableSetOf<String>()
    val normalizedLatitude = latitude.trim()
    val normalizedLongitude = longitude.trim()
    val normalizedAltitude = altitude.trim()

    val latitudeValue = normalizedLatitude.toDoubleOrNull()
    if (latitudeValue == null || latitudeValue !in -90.0..90.0) {
        invalidFields += FIELD_GEO_LATITUDE
    }

    val longitudeValue = normalizedLongitude.toDoubleOrNull()
    if (longitudeValue == null || longitudeValue !in -180.0..180.0) {
        invalidFields += FIELD_GEO_LONGITUDE
    }

    val altitudeValue = if (normalizedAltitude.isEmpty()) {
        null
    } else {
        normalizedAltitude.toDoubleOrNull()?.also { _ -> }
            ?: run {
                invalidFields += FIELD_GEO_ALTITUDE
                null
            }
    }

    if (invalidFields.isNotEmpty()) {
        return FactoryValidation(invalidFields = invalidFields)
    }

    val content = buildString {
        append("geo:")
        append(latitudeValue)
        append(",")
        append(longitudeValue)
        altitudeValue?.let {
            append(",")
            append(it)
        }
    }

    return FactoryValidation(content = content)
}

fun parseEventDate(value: String) =
    value.takeIf { it.isNotEmpty() }?.let {
        SimpleDateFormat(EVENT_INPUT_PATTERN, Locale.getDefault()).apply {
            isLenient = false
        }.parse(it)
    }

fun parseEventAllDayDate(value: String) =
    value.takeIf { it.isNotEmpty() }?.let {
        SimpleDateFormat(EVENT_ALL_DAY_INPUT_PATTERN, Locale.getDefault()).apply {
            isLenient = false
        }.parse(it)
    }

fun formatEventDate(value: Date): String =
    SimpleDateFormat(EVENT_INPUT_PATTERN, Locale.getDefault()).format(value)

fun formatEventAllDayDate(value: Date): String =
    SimpleDateFormat(EVENT_ALL_DAY_INPUT_PATTERN, Locale.getDefault()).format(value)

private fun escapeWifiValue(value: String): String =
    value
        .replace("\\", "\\\\")
        .replace(";", "\\;")
        .replace(",", "\\,")
        .replace(":", "\\:")
        .replace("\"", "\\\"")

private fun escapeIcsText(value: String): String =
    value
        .replace("\\", "\\\\")
        .replace("\n", "\\n")
        .replace(",", "\\,")
        .replace(";", "\\;")

private fun normalizeValidPhoneNumber(phoneNumber: String): String? {
    val rawPhone = phoneNumber.trim()
    val normalizedPhone = PhoneNumberUtils.normalizeNumber(rawPhone)
    val digitCount = normalizedPhone.count(Char::isDigit)
    return normalizedPhone.takeIf {
        rawPhone.isNotEmpty() &&
            normalizedPhone.isNotEmpty() &&
            digitCount >= 3 &&
            PhoneNumberUtils.isGlobalPhoneNumber(normalizedPhone)
    }
}

data class FactoryValidation(
    val content: String? = null,
    val invalidFields: Set<String> = emptySet()
)

enum class FactoryType(
    val labelRes: Int,
    val descriptionRes: Int,
) {
    Wifi(
        labelRes = R.string.label_generate_wifi_sharing,
        descriptionRes = R.string.tip_factory_wifi_desc
    ),
    Email(
        labelRes = R.string.label_factory_type_email,
        descriptionRes = R.string.tip_factory_email_desc
    ),
    Event(
        labelRes = R.string.label_factory_type_event,
        descriptionRes = R.string.tip_factory_event_desc
    ),
    Phone(
        labelRes = R.string.label_factory_type_phone,
        descriptionRes = R.string.tip_factory_phone_desc
    ),
    Message(
        labelRes = R.string.label_factory_type_message,
        descriptionRes = R.string.tip_factory_message_desc
    ),
    Geo(
        labelRes = R.string.label_factory_type_geo,
        descriptionRes = R.string.tip_factory_geo_desc
    ),
    Application(
        labelRes = R.string.label_factory_type_application,
        descriptionRes = R.string.tip_factory_app_desc
    ),
}

enum class WifiSecurity(
    val labelRes: Int,
    val protocol: String,
) {
    WPA(labelRes = R.string.label_factory_wifi_security_wpa, protocol = "WPA"),
    WPA3(labelRes = R.string.label_factory_wifi_security_wpa3, protocol = "WPA"),
    WEP(labelRes = R.string.label_factory_wifi_security_wep, protocol = "WEP"),
    None(labelRes = R.string.none, protocol = "nopass"),
}

private const val EVENT_INPUT_PATTERN = "yyyy-MM-dd HH:mm"
private const val EVENT_OUTPUT_PATTERN = "yyyyMMdd'T'HHmmss"
private const val EVENT_ALL_DAY_INPUT_PATTERN = "yyyy-MM-dd"
private const val EVENT_ALL_DAY_OUTPUT_PATTERN = "yyyyMMdd"
private const val WIFI_PASSWORD_MIN_LENGTH = 8

private val PACKAGE_NAME_REGEX = Regex("^[A-Za-z][A-Za-z0-9_]*(\\.[A-Za-z][A-Za-z0-9_]*)+$")
