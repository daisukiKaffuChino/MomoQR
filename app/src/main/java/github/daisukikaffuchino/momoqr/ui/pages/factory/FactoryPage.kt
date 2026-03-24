package github.daisukikaffuchino.momoqr.ui.pages.factory

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.logic.database.StarEntity
import github.daisukikaffuchino.momoqr.logic.model.FactoryType
import github.daisukikaffuchino.momoqr.ui.components.TopAppBarScaffold
import github.daisukikaffuchino.momoqr.ui.pages.factory.components.ApplicationForm
import github.daisukikaffuchino.momoqr.ui.pages.factory.components.EmailForm
import github.daisukikaffuchino.momoqr.ui.pages.factory.components.EventForm
import github.daisukikaffuchino.momoqr.ui.pages.factory.components.FactoryDateTimePickerDialogs
import github.daisukikaffuchino.momoqr.ui.pages.factory.components.GeoForm
import github.daisukikaffuchino.momoqr.ui.pages.factory.components.MessageForm
import github.daisukikaffuchino.momoqr.ui.pages.factory.components.PhoneForm
import github.daisukikaffuchino.momoqr.ui.pages.factory.components.TypeChipsCard
import github.daisukikaffuchino.momoqr.ui.pages.factory.components.WifiForm
import github.daisukikaffuchino.momoqr.ui.pages.result.components.ResultFloatingActionButton
import github.daisukikaffuchino.momoqr.ui.theme.Defaults
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@SuppressLint("LocalContextGetResourceValueCall")
@OptIn(ExperimentalLayoutApi::class)
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
                TypeChipsCard(
                    selectedType = selectedType,
                    viewModel = viewModel
                )
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



data class FactoryValidation(
    val content: String? = null,
    val invalidFields: Set<String> = emptySet()
)


enum class WifiSecurity(
    val labelRes: Int,
    val protocol: String,
) {
    WPA(labelRes = R.string.label_factory_wifi_security_wpa, protocol = "WPA"),
    WPA3(labelRes = R.string.label_factory_wifi_security_wpa3, protocol = "SAE"),
    WEP(labelRes = R.string.label_factory_wifi_security_wep, protocol = "WEP"),
    None(labelRes = R.string.none, protocol = "nopass"),
}

private const val EVENT_INPUT_PATTERN = "yyyy-MM-dd HH:mm"

private const val EVENT_ALL_DAY_INPUT_PATTERN = "yyyy-MM-dd"

