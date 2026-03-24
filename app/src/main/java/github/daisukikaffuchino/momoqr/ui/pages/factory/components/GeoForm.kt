package github.daisukikaffuchino.momoqr.ui.pages.factory.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.ui.pages.factory.FIELD_GEO_ALTITUDE
import github.daisukikaffuchino.momoqr.ui.pages.factory.FIELD_GEO_LATITUDE
import github.daisukikaffuchino.momoqr.ui.pages.factory.FIELD_GEO_LONGITUDE

@Composable
fun GeoForm(
    latitude: String,
    onLatitudeChange: (String) -> Unit,
    longitude: String,
    onLongitudeChange: (String) -> Unit,
    altitude: String,
    onAltitudeChange: (String) -> Unit,
    invalidFields: Set<String>,
    shouldShowErrors: Boolean,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        FactoryTextField(
            value = latitude,
            onValueChange = onLatitudeChange,
            label = stringResource(R.string.label_factory_geo_latitude),
            supportingText = stringResource(R.string.tip_factory_geo_latitude),
            isError = shouldShowErrors && FIELD_GEO_LATITUDE in invalidFields,
            keyboardType = KeyboardType.Decimal
        )

        FactoryTextField(
            value = longitude,
            onValueChange = onLongitudeChange,
            label = stringResource(R.string.label_factory_geo_longitude),
            supportingText = stringResource(R.string.tip_factory_geo_longitude),
            isError = shouldShowErrors && FIELD_GEO_LONGITUDE in invalidFields,
            keyboardType = KeyboardType.Decimal
        )

        FactoryTextField(
            value = altitude,
            onValueChange = onAltitudeChange,
            label = stringResource(R.string.label_factory_geo_altitude),
            supportingText = stringResource(R.string.tip_factory_geo_altitude),
            isError = shouldShowErrors && FIELD_GEO_ALTITUDE in invalidFields,
            keyboardType = KeyboardType.Decimal
        )
    }
}
