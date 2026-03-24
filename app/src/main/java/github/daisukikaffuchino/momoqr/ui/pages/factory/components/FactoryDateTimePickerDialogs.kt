package github.daisukikaffuchino.momoqr.ui.pages.factory.components

import android.text.format.DateFormat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.ui.pages.factory.FactoryUiState
import java.util.Calendar

@Composable
fun EventDateTimeField(
    value: String,
    label: String,
    supportingText: String,
    isError: Boolean,
    onClick: () -> Unit,
) {
    FactoryPickerField(
        value = value,
        label = label,
        supportingText = supportingText,
        isError = isError,
        onClick = onClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FactoryDateTimePickerDialogs(
    state: FactoryUiState,
    onDismissDatePicker: () -> Unit,
    onConfirmDatePicker: (Long?) -> Unit,
    onDismissTimePicker: () -> Unit,
    onConfirmTimePicker: (Int, Int) -> Unit,
) {
    val context = LocalContext.current

    if (state.showEventDatePicker) {
        val datePickerState = androidx.compose.material3.rememberDatePickerState(
            initialSelectedDateMillis = state.pendingDateMillis ?: System.currentTimeMillis()
        )

        DatePickerDialog(
            onDismissRequest = onDismissDatePicker,
            confirmButton = {
                TextButton(
                    onClick = { onConfirmDatePicker(datePickerState.selectedDateMillis) }
                ) {
                    Text(text = stringResource(R.string.action_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissDatePicker) {
                    Text(text = stringResource(R.string.action_cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (state.showEventTimePicker) {
        val calendar = remember(state.pendingDateMillis) {
            Calendar.getInstance().apply {
                timeInMillis = state.pendingDateMillis ?: System.currentTimeMillis()
            }
        }
        val timePickerState = androidx.compose.material3.rememberTimePickerState(
            initialHour = calendar.get(Calendar.HOUR_OF_DAY),
            initialMinute = calendar.get(Calendar.MINUTE),
            is24Hour = DateFormat.is24HourFormat(context)
        )

        Dialog(onDismissRequest = onDismissTimePicker) {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Text(
                        text = stringResource(
                            if (state.eventAllDay) {
                                R.string.tip_factory_pick_date
                            } else {
                                R.string.tip_factory_pick_datetime
                            }
                        ),
                        style = MaterialTheme.typography.titleLarge
                    )
                    TimePicker(
                        state = timePickerState,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismissTimePicker) {
                            Text(text = stringResource(R.string.action_cancel))
                        }
                        TextButton(
                            onClick = {
                                onConfirmTimePicker(
                                    timePickerState.hour,
                                    timePickerState.minute
                                )
                            }
                        ) {
                            Text(text = stringResource(R.string.action_confirm))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FactoryPickerField(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    isError: Boolean = false,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            label = { Text(text = label) },
            supportingText = supportingText?.let {
                { Text(text = it) }
            },
            modifier = Modifier.fillMaxWidth(),
            isError = isError,
            readOnly = true,
            singleLine = true
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onClick)
        )
    }
}
