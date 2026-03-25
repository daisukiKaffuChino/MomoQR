package github.daisukikaffuchino.momoqr.logic.model

import github.daisukikaffuchino.momoqr.R

enum class FactoryType(
    val labelRes: Int,
    val descriptionRes: Int,
    val iconRes: Int,
) {
    Wifi(
        labelRes = R.string.label_generate_wifi,
        descriptionRes = R.string.tip_factory_wifi_desc,
        iconRes = R.drawable.ic_wifi
    ),
    Email(
        labelRes = R.string.label_factory_type_email,
        descriptionRes = R.string.tip_factory_email_desc,
        iconRes = R.drawable.ic_mail
    ),
    Event(
        labelRes = R.string.label_factory_type_event,
        descriptionRes = R.string.tip_factory_event_desc,
        iconRes = R.drawable.ic_event_note
    ),
    Phone(
        labelRes = R.string.label_factory_type_phone,
        descriptionRes = R.string.tip_factory_phone_desc,
        iconRes = R.drawable.ic_call
    ),
    Message(
        labelRes = R.string.label_factory_type_sms,
        descriptionRes = R.string.tip_factory_message_desc,
        iconRes = R.drawable.ic_sms
    ),
    Geo(
        labelRes = R.string.label_factory_type_geo,
        descriptionRes = R.string.tip_factory_geo_desc,
        iconRes = R.drawable.ic_location_on
    ),
    Application(
        labelRes = R.string.label_factory_type_application,
        descriptionRes = R.string.tip_factory_app_desc,
        iconRes = R.drawable.ic_grid_view
    ),
}