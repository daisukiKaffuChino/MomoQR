package github.daisukikaffuchino.momoqr.ui.pages.factory.components

import android.text.format.DateFormat
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.ui.pages.factory.FactoryUiState
import github.daisukikaffuchino.momoqr.utils.VibrationUtil
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

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun FactoryDateTimePickerDialogs(
    state: FactoryUiState,
    onDismissDatePicker: () -> Unit,
    onConfirmDatePicker: (Long?) -> Unit,
    onDismissTimePicker: () -> Unit,
    onConfirmTimePicker: (Int, Int) -> Unit,
) {
    val context = LocalContext.current
    val view = LocalView.current

    if (state.showEventDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = state.pendingDateMillis ?: System.currentTimeMillis()
        )

        DatePickerDialog(
            onDismissRequest = {
                onConfirmDatePicker(null)
            },
            confirmButton = {
                FilledTonalButton(
                    onClick = {
                        VibrationUtil.performHapticFeedback(view)
                        onConfirmDatePicker(datePickerState.selectedDateMillis)
                    },
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text(text = stringResource(R.string.action_confirm))
                }
            },
            dismissButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(
                        onClick = {
                            VibrationUtil.performHapticFeedback(view)
                            datePickerState.selectedDateMillis = null
                        },
                        shapes = ButtonDefaults.shapes()
                    ) {
                        Text(text = stringResource(R.string.action_clear))
                    }
                    TextButton(
                        onClick = {
                            VibrationUtil.performHapticFeedback(view)
                            onDismissDatePicker()
                        },
                        shapes = ButtonDefaults.shapes()
                    ) {
                        Text(text = stringResource(R.string.action_cancel))
                    }
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                modifier = Modifier.verticalScroll(rememberScrollState())
            )
        }
    }

    if (state.showEventTimePicker) {
        val calendar = remember(state.pendingDateMillis) {
            Calendar.getInstance().apply {
                timeInMillis = state.pendingDateMillis ?: System.currentTimeMillis()
            }
        }
        val timePickerState = rememberTimePickerState(
            initialHour = calendar.get(Calendar.HOUR_OF_DAY),
            initialMinute = calendar.get(Calendar.MINUTE),
            is24Hour = DateFormat.is24HourFormat(context)
        )

        DatePickerDialog(
            onDismissRequest = onDismissTimePicker,
            confirmButton = {
                FilledTonalButton(
                    onClick = {
                        VibrationUtil.performHapticFeedback(view)
                        onConfirmTimePicker(
                            timePickerState.hour,
                            timePickerState.minute
                        )
                    },
                    shapes = ButtonDefaults.shapes()
                ) {
                    Text(text = stringResource(R.string.action_confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        VibrationUtil.performHapticFeedback(view)
                        onDismissTimePicker()
                    },
                    shapes = ButtonDefaults.shapes()
                ) {
                    Text(text = stringResource(R.string.action_cancel))
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(
                        if (state.eventAllDay) {
                            R.string.tip_factory_pick_date
                        } else {
                            R.string.tip_factory_pick_datetime
                        }
                    ),
                    style = MaterialTheme.typography.titleSmall
                )
                TimePicker(
                    state = timePickerState,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
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
    val view = LocalView.current
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    LaunchedEffect(pressed) {
        if (pressed) {
            VibrationUtil.performHapticFeedback(view)
            onClick()
        }
    }

    OutlinedTextField(
        value = value,
        onValueChange = {},
        label = { Text(text = label) },
        supportingText = supportingText?.let {
            { Text(text = it) }
        },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        isError = isError,
        readOnly = true,
        singleLine = true,
        interactionSource = interactionSource
    )
}
