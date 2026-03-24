package github.daisukikaffuchino.momoqr.ui.pages.factory

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class FactoryViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _state = MutableStateFlow(
        FactoryUiState(
            selectedTypeName = savedStateHandle[KEY_SELECTED_TYPE] ?: FactoryType.Wifi.name,
            shouldShowErrors = savedStateHandle[KEY_SHOULD_SHOW_ERRORS] ?: false,
            wifiSsid = savedStateHandle[KEY_WIFI_SSID] ?: "",
            wifiPassword = savedStateHandle[KEY_WIFI_PASSWORD] ?: "",
            wifiSecurityName = savedStateHandle[KEY_WIFI_SECURITY] ?: WifiSecurity.WPA.name,
            wifiHidden = savedStateHandle[KEY_WIFI_HIDDEN] ?: false,
            emailAddress = savedStateHandle[KEY_EMAIL_ADDRESS] ?: "",
            emailSubject = savedStateHandle[KEY_EMAIL_SUBJECT] ?: "",
            emailBody = savedStateHandle[KEY_EMAIL_BODY] ?: "",
            eventTitle = savedStateHandle[KEY_EVENT_TITLE] ?: "",
            eventAllDay = savedStateHandle[KEY_EVENT_ALL_DAY] ?: false,
            eventStart = savedStateHandle[KEY_EVENT_START] ?: "",
            eventEnd = savedStateHandle[KEY_EVENT_END] ?: "",
            eventLocation = savedStateHandle[KEY_EVENT_LOCATION] ?: "",
            eventDescription = savedStateHandle[KEY_EVENT_DESCRIPTION] ?: "",
            phoneNumber = savedStateHandle[KEY_PHONE_NUMBER] ?: "",
            messagePhone = savedStateHandle[KEY_MESSAGE_PHONE] ?: "",
            messageBody = savedStateHandle[KEY_MESSAGE_BODY] ?: "",
            appPackageName = savedStateHandle[KEY_APP_PACKAGE] ?: "",
            geoLatitude = savedStateHandle[KEY_GEO_LATITUDE] ?: "",
            geoLongitude = savedStateHandle[KEY_GEO_LONGITUDE] ?: "",
            geoAltitude = savedStateHandle[KEY_GEO_ALTITUDE] ?: "",
            showEventDatePicker = savedStateHandle[KEY_SHOW_EVENT_DATE_PICKER] ?: false,
            showEventTimePicker = savedStateHandle[KEY_SHOW_EVENT_TIME_PICKER] ?: false,
            pendingDateMillis = savedStateHandle[KEY_PENDING_DATE_MILLIS],
            activeEventField = savedStateHandle.get<String>(KEY_ACTIVE_EVENT_FIELD)
                ?.let(EventDateTimeTarget::valueOf),
        )
            .withValidation()
    )
    val state = _state.asStateFlow()

    fun updateSelectedType(type: FactoryType) {
        updateState {
            it.copy(
                selectedTypeName = type.name,
                shouldShowErrors = false,
            )
        }
    }

    fun showErrors() {
        updateState { it.copy(shouldShowErrors = true) }
    }

    fun updateWifiSsid(value: String) = updateState { it.copy(wifiSsid = value) }
    fun updateWifiPassword(value: String) = updateState { it.copy(wifiPassword = value) }
    fun updateWifiSecurity(value: WifiSecurity) = updateState { it.copy(wifiSecurityName = value.name) }
    fun updateWifiHidden(value: Boolean) = updateState { it.copy(wifiHidden = value) }

    fun updateEmailAddress(value: String) = updateState { it.copy(emailAddress = value) }
    fun updateEmailSubject(value: String) = updateState { it.copy(emailSubject = value) }
    fun updateEmailBody(value: String) = updateState { it.copy(emailBody = value) }

    fun updateEventTitle(value: String) = updateState { it.copy(eventTitle = value) }
    fun updateEventAllDay(value: Boolean) {
        val currentState = state.value
        if (value) {
            val baseDate = parseEventDate(currentState.eventStart)
                ?: parseEventAllDayDate(currentState.eventStart)
            updateState {
                it.copy(
                    eventAllDay = true,
                    eventStart = baseDate?.let(::formatEventAllDayDate).orEmpty(),
                    eventEnd = "",
                    showEventTimePicker = false,
                    activeEventField = null,
                    pendingDateMillis = null,
                )
            }
        } else {
            val date = parseEventAllDayDate(currentState.eventStart)
            val startCalendar = Calendar.getInstance().apply {
                if (date != null) {
                    time = date
                }
                set(Calendar.HOUR_OF_DAY, 9)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val endCalendar = (startCalendar.clone() as Calendar).apply {
                add(Calendar.HOUR_OF_DAY, 1)
            }
            updateState {
                it.copy(
                    eventAllDay = false,
                    eventStart = formatEventDate(startCalendar.time),
                    eventEnd = formatEventDate(endCalendar.time),
                )
            }
        }
    }
    fun updateEventLocation(value: String) = updateState { it.copy(eventLocation = value) }
    fun updateEventDescription(value: String) = updateState { it.copy(eventDescription = value) }

    fun updatePhoneNumber(value: String) = updateState { it.copy(phoneNumber = value) }
    fun updateMessagePhone(value: String) = updateState { it.copy(messagePhone = value) }
    fun updateMessageBody(value: String) = updateState { it.copy(messageBody = value) }

    fun updateAppPackageName(value: String) = updateState { it.copy(appPackageName = value) }
    fun updateGeoLatitude(value: String) = updateState { it.copy(geoLatitude = value) }
    fun updateGeoLongitude(value: String) = updateState { it.copy(geoLongitude = value) }
    fun updateGeoAltitude(value: String) = updateState { it.copy(geoAltitude = value) }

    fun openEventDatePicker(target: EventDateTimeTarget) {
        val currentValue = when (target) {
            EventDateTimeTarget.Start -> state.value.eventStart
            EventDateTimeTarget.End -> state.value.eventEnd
        }
        updateState {
            it.copy(
                showEventDatePicker = true,
                showEventTimePicker = false,
                activeEventField = target,
                pendingDateMillis = parseEventPickerValue(
                    value = currentValue,
                    allDay = it.eventAllDay,
                )?.time ?: System.currentTimeMillis(),
            )
        }
    }

    fun dismissEventDatePicker() {
        updateState {
            it.copy(
                showEventDatePicker = false,
                showEventTimePicker = false,
                activeEventField = null,
                pendingDateMillis = null,
            )
        }
    }

    fun confirmEventDateSelection(selectedDateMillis: Long?) {
        val currentState = state.value
        val pickedMillis = selectedDateMillis ?: currentState.pendingDateMillis ?: System.currentTimeMillis()
        if (currentState.eventAllDay) {
            val pickedValue = formatEventAllDayDate(java.util.Date(pickedMillis))
            updateState {
                when (currentState.activeEventField) {
                    EventDateTimeTarget.Start, null -> it.copy(
                        eventStart = pickedValue,
                        showEventDatePicker = false,
                        showEventTimePicker = false,
                        activeEventField = null,
                        pendingDateMillis = null,
                    )

                    EventDateTimeTarget.End -> it.copy(
                        showEventDatePicker = false,
                        showEventTimePicker = false,
                        activeEventField = null,
                        pendingDateMillis = null,
                    )
                }
            }
        } else {
            updateState {
                it.copy(
                    showEventDatePicker = false,
                    showEventTimePicker = true,
                    pendingDateMillis = pickedMillis,
                )
            }
        }
    }

    fun dismissEventTimePicker() {
        updateState {
            it.copy(
                showEventTimePicker = false,
                activeEventField = null,
                pendingDateMillis = null,
            )
        }
    }

    fun confirmEventTimeSelection(hour: Int, minute: Int) {
        val currentState = state.value
        val calendar = Calendar.getInstance().apply {
            timeInMillis = currentState.pendingDateMillis ?: System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val value = formatEventDate(calendar.time)

        updateState {
            when (currentState.activeEventField) {
                EventDateTimeTarget.Start -> it.copy(
                    eventStart = value,
                    showEventTimePicker = false,
                    activeEventField = null,
                    pendingDateMillis = null,
                )

                EventDateTimeTarget.End -> it.copy(
                    eventEnd = value,
                    showEventTimePicker = false,
                    activeEventField = null,
                    pendingDateMillis = null,
                )

                null -> it.copy(
                    showEventTimePicker = false,
                    activeEventField = null,
                    pendingDateMillis = null,
                )
            }
        }
    }

    private fun updateState(transform: (FactoryUiState) -> FactoryUiState) {
        _state.update { current -> transform(current).withValidation() }
        persistState(_state.value)
    }

    private fun persistState(state: FactoryUiState) {
        savedStateHandle[KEY_SELECTED_TYPE] = state.selectedTypeName
        savedStateHandle[KEY_SHOULD_SHOW_ERRORS] = state.shouldShowErrors
        savedStateHandle[KEY_WIFI_SSID] = state.wifiSsid
        savedStateHandle[KEY_WIFI_PASSWORD] = state.wifiPassword
        savedStateHandle[KEY_WIFI_SECURITY] = state.wifiSecurityName
        savedStateHandle[KEY_WIFI_HIDDEN] = state.wifiHidden
        savedStateHandle[KEY_EMAIL_ADDRESS] = state.emailAddress
        savedStateHandle[KEY_EMAIL_SUBJECT] = state.emailSubject
        savedStateHandle[KEY_EMAIL_BODY] = state.emailBody
        savedStateHandle[KEY_EVENT_TITLE] = state.eventTitle
        savedStateHandle[KEY_EVENT_ALL_DAY] = state.eventAllDay
        savedStateHandle[KEY_EVENT_START] = state.eventStart
        savedStateHandle[KEY_EVENT_END] = state.eventEnd
        savedStateHandle[KEY_EVENT_LOCATION] = state.eventLocation
        savedStateHandle[KEY_EVENT_DESCRIPTION] = state.eventDescription
        savedStateHandle[KEY_PHONE_NUMBER] = state.phoneNumber
        savedStateHandle[KEY_MESSAGE_PHONE] = state.messagePhone
        savedStateHandle[KEY_MESSAGE_BODY] = state.messageBody
        savedStateHandle[KEY_APP_PACKAGE] = state.appPackageName
        savedStateHandle[KEY_GEO_LATITUDE] = state.geoLatitude
        savedStateHandle[KEY_GEO_LONGITUDE] = state.geoLongitude
        savedStateHandle[KEY_GEO_ALTITUDE] = state.geoAltitude
        savedStateHandle[KEY_SHOW_EVENT_DATE_PICKER] = state.showEventDatePicker
        savedStateHandle[KEY_SHOW_EVENT_TIME_PICKER] = state.showEventTimePicker
        savedStateHandle[KEY_PENDING_DATE_MILLIS] = state.pendingDateMillis
        savedStateHandle[KEY_ACTIVE_EVENT_FIELD] = state.activeEventField?.name
    }
}

data class FactoryUiState(
    val selectedTypeName: String = FactoryType.Wifi.name,
    val shouldShowErrors: Boolean = false,
    val wifiSsid: String = "",
    val wifiPassword: String = "",
    val wifiSecurityName: String = WifiSecurity.WPA.name,
    val wifiHidden: Boolean = false,
    val emailAddress: String = "",
    val emailSubject: String = "",
    val emailBody: String = "",
    val eventTitle: String = "",
    val eventAllDay: Boolean = false,
    val eventStart: String = "",
    val eventEnd: String = "",
    val eventLocation: String = "",
    val eventDescription: String = "",
    val phoneNumber: String = "",
    val messagePhone: String = "",
    val messageBody: String = "",
    val appPackageName: String = "",
    val geoLatitude: String = "",
    val geoLongitude: String = "",
    val geoAltitude: String = "",
    val showEventDatePicker: Boolean = false,
    val showEventTimePicker: Boolean = false,
    val pendingDateMillis: Long? = null,
    val activeEventField: EventDateTimeTarget? = null,
    val validation: FactoryValidation = FactoryValidation(),
)

enum class EventDateTimeTarget {
    Start,
    End,
}

private fun FactoryUiState.withValidation(): FactoryUiState {
    val selectedType = FactoryType.valueOf(selectedTypeName)
    val validation = when (selectedType) {
        FactoryType.Wifi -> buildWifiPayload(
            ssid = wifiSsid,
            password = wifiPassword,
            security = WifiSecurity.valueOf(wifiSecurityName),
            hidden = wifiHidden,
        )

        FactoryType.Email -> buildEmailPayload(
            emailAddress = emailAddress,
            subject = emailSubject,
            body = emailBody,
        )

        FactoryType.Event -> buildEventPayload(
            title = eventTitle,
            allDay = eventAllDay,
            start = eventStart,
            end = eventEnd,
            location = eventLocation,
            description = eventDescription,
        )

        FactoryType.Phone -> buildPhonePayload(phoneNumber = phoneNumber)
        FactoryType.Message -> buildMessagePayload(
            phoneNumber = messagePhone,
            body = messageBody,
        )

        FactoryType.Application -> buildApplicationPayload(
            packageName = appPackageName,
        )

        FactoryType.Geo -> buildGeoPayload(
            latitude = geoLatitude,
            longitude = geoLongitude,
            altitude = geoAltitude,
        )
    }
    return copy(validation = validation)
}

private const val KEY_SELECTED_TYPE = "selected_type"
private const val KEY_SHOULD_SHOW_ERRORS = "should_show_errors"
private const val KEY_WIFI_SSID = "wifi_ssid"
private const val KEY_WIFI_PASSWORD = "wifi_password"
private const val KEY_WIFI_SECURITY = "wifi_security"
private const val KEY_WIFI_HIDDEN = "wifi_hidden"
private const val KEY_EMAIL_ADDRESS = "email_address"
private const val KEY_EMAIL_SUBJECT = "email_subject"
private const val KEY_EMAIL_BODY = "email_body"
private const val KEY_EVENT_TITLE = "event_title"
private const val KEY_EVENT_ALL_DAY = "event_all_day"
private const val KEY_EVENT_START = "event_start"
private const val KEY_EVENT_END = "event_end"
private const val KEY_EVENT_LOCATION = "event_location"
private const val KEY_EVENT_DESCRIPTION = "event_description"
private const val KEY_PHONE_NUMBER = "phone_number"
private const val KEY_MESSAGE_PHONE = "message_phone"
private const val KEY_MESSAGE_BODY = "message_body"
private const val KEY_APP_PACKAGE = "app_package"
private const val KEY_GEO_LATITUDE = "geo_latitude"
private const val KEY_GEO_LONGITUDE = "geo_longitude"
private const val KEY_GEO_ALTITUDE = "geo_altitude"
private const val KEY_SHOW_EVENT_DATE_PICKER = "show_event_date_picker"
private const val KEY_SHOW_EVENT_TIME_PICKER = "show_event_time_picker"
private const val KEY_PENDING_DATE_MILLIS = "pending_date_millis"
private const val KEY_ACTIVE_EVENT_FIELD = "active_event_field"

private fun parseEventPickerValue(
    value: String,
    allDay: Boolean,
) = if (allDay) parseEventAllDayDate(value) else parseEventDate(value)
