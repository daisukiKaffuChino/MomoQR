package github.daisukikaffuchino.momoqr.ui.pages.factory

import android.net.Uri
import android.telephony.PhoneNumberUtils
import android.util.Patterns
import github.daisukikaffuchino.momoqr.logic.model.WifiSecurity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private val PACKAGE_NAME_REGEX = Regex("^[A-Za-z][A-Za-z0-9_]*(\\.[A-Za-z][A-Za-z0-9_]*)+$")
private const val EVENT_OUTPUT_PATTERN = "yyyyMMdd'T'HHmmss"
private const val EVENT_ALL_DAY_OUTPUT_PATTERN = "yyyyMMdd"
private const val WIFI_PASSWORD_MIN_LENGTH = 8

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
    val normalizedLocation = location.trim()
    val normalizedDescription = description.trim()

    if (normalizedTitle.isEmpty()) {
        invalidFields += FIELD_EVENT_TITLE
    }

    val content = if (allDay) {
        val startDate = parseEventAllDayDate(normalizedStart)
        if (startDate == null) {
            invalidFields += FIELD_EVENT_START
        }

        if (invalidFields.isNotEmpty()) {
            return FactoryValidation(invalidFields = invalidFields)
        }

        val validStartDate = startDate ?: return FactoryValidation(invalidFields = setOf(FIELD_EVENT_START))
        val formatter = SimpleDateFormat(EVENT_ALL_DAY_OUTPUT_PATTERN, Locale.getDefault())
        val endCalendar = Calendar.getInstance().apply {
            time = validStartDate
            add(Calendar.DAY_OF_YEAR, 1)
        }

        buildString {
            appendLine("BEGIN:VEVENT")
            appendLine("SUMMARY:${escapeIcsText(normalizedTitle)}")
            appendLine("DTSTART;VALUE=DATE:${formatter.format(validStartDate)}")
            appendLine("DTEND;VALUE=DATE:${formatter.format(endCalendar.time)}")
            normalizedLocation.takeIf { it.isNotEmpty() }?.let {
                appendLine("LOCATION:${escapeIcsText(it)}")
            }
            normalizedDescription.takeIf { it.isNotEmpty() }?.let {
                appendLine("DESCRIPTION:${escapeIcsText(it)}")
            }
            append("END:VEVENT")
        }
    } else {
        val startDate = parseEventDate(normalizedStart)
        if (startDate == null) {
            invalidFields += FIELD_EVENT_START
        }

        val endDate = parseEventDate(normalizedEnd)
        if (endDate == null) {
            invalidFields += FIELD_EVENT_END
        }

        if (startDate != null && endDate != null && endDate.before(startDate)) {
            invalidFields += FIELD_EVENT_END
        }

        if (invalidFields.isNotEmpty()) {
            return FactoryValidation(invalidFields = invalidFields)
        }

        val validStartDate = startDate ?: return FactoryValidation(invalidFields = setOf(FIELD_EVENT_START))
        val validEndDate = endDate ?: return FactoryValidation(invalidFields = setOf(FIELD_EVENT_END))
        val formatter = SimpleDateFormat(EVENT_OUTPUT_PATTERN, Locale.getDefault())

        buildString {
            appendLine("BEGIN:VEVENT")
            appendLine("SUMMARY:${escapeIcsText(normalizedTitle)}")
            appendLine("DTSTART:${formatter.format(validStartDate)}")
            appendLine("DTEND:${formatter.format(validEndDate)}")
            normalizedLocation.takeIf { it.isNotEmpty() }?.let {
                appendLine("LOCATION:${escapeIcsText(it)}")
            }
            normalizedDescription.takeIf { it.isNotEmpty() }?.let {
                appendLine("DESCRIPTION:${escapeIcsText(it)}")
            }
            append("END:VEVENT")
        }
    }

    return FactoryValidation(content = content)
}

fun buildPhonePayload(phoneNumber: String): FactoryValidation {
    val normalizedPhone = normalizeValidPhoneNumber(phoneNumber) ?: return FactoryValidation(
        invalidFields = setOf(FIELD_PHONE_NUMBER)
    )
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
        normalizedAltitude.toDoubleOrNull()
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