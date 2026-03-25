package github.daisukikaffuchino.momoqr.ui.pages.factory.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.ui.components.TextCheckbox
import github.daisukikaffuchino.momoqr.ui.pages.factory.FIELD_EVENT_END
import github.daisukikaffuchino.momoqr.ui.pages.factory.FIELD_EVENT_START
import github.daisukikaffuchino.momoqr.ui.pages.factory.FIELD_EVENT_TITLE

@Composable
fun EventForm(
    title: String,
    onTitleChange: (String) -> Unit,
    allDay: Boolean,
    onAllDayChange: (Boolean) -> Unit,
    start: String,
    onStartClick: () -> Unit,
    end: String,
    onEndClick: () -> Unit,
    location: String,
    onLocationChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    invalidFields: Set<String>,
    shouldShowErrors: Boolean,
) {
    Column(
        modifier = Modifier.animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FactoryTextField(
            value = title,
            onValueChange = onTitleChange,
            label = stringResource(R.string.label_factory_event_title),
            isError = shouldShowErrors && FIELD_EVENT_TITLE in invalidFields
        )

        TextCheckbox(
            checked = allDay,
            onCheckedChange = onAllDayChange,
            stringRes = R.string.label_factory_event_all_day,
            modifier = Modifier.fillMaxWidth()
        )

        if (allDay) {
            EventDateTimeField(
                value = start,
                label = stringResource(R.string.label_factory_event_date),
                supportingText = stringResource(R.string.tip_factory_pick_date),
                isError = shouldShowErrors && FIELD_EVENT_START in invalidFields,
                onClick = onStartClick
            )
        } else {
            EventDateTimeField(
                value = start,
                label = stringResource(R.string.label_factory_event_start),
                supportingText = stringResource(R.string.tip_factory_pick_datetime),
                isError = shouldShowErrors && FIELD_EVENT_START in invalidFields,
                onClick = onStartClick
            )

            EventDateTimeField(
                value = end,
                label = stringResource(R.string.label_factory_event_end),
                supportingText = stringResource(R.string.tip_factory_pick_datetime),
                isError = shouldShowErrors && FIELD_EVENT_END in invalidFields,
                onClick = onEndClick
            )
        }

        FactoryTextField(
            value = location,
            onValueChange = onLocationChange,
            label = stringResource(R.string.label_factory_event_location)
        )

        FactoryTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = stringResource(R.string.label_factory_event_description),
            minLines = 4
        )
    }
}
