package github.daisukikaffuchino.momoqr.logic.model

import github.daisukikaffuchino.momoqr.R

enum class FactoryType(
    val labelRes: Int,
    val iconRes: Int
) {
    Wifi(
        labelRes = R.string.label_generate_wifi,
        iconRes = R.drawable.ic_wifi
    ),
    Email(
        labelRes = R.string.label_factory_type_email,
        iconRes = R.drawable.ic_mail
    ),
    Event(
        labelRes = R.string.label_factory_type_event,
        iconRes = R.drawable.ic_event_note
    ),
    Phone(
        labelRes = R.string.label_factory_type_phone,
        iconRes = R.drawable.ic_call
    ),
    Message(
        labelRes = R.string.label_factory_type_sms,
        iconRes = R.drawable.ic_sms
    ),
    Geo(
        labelRes = R.string.label_factory_type_geo,
        iconRes = R.drawable.ic_location_on
    ),
    Application(
        labelRes = R.string.label_factory_type_application,
        iconRes = R.drawable.ic_grid_view
    ),
}