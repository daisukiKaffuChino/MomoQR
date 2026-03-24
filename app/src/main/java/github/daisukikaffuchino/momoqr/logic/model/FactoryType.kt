package github.daisukikaffuchino.momoqr.logic.model

import github.daisukikaffuchino.momoqr.R

enum class FactoryType(
    val labelRes: Int,
    val descriptionRes: Int,
    val iconRes: Int,
) {
    Wifi(
        labelRes = R.string.label_generate_wifi_sharing,
        descriptionRes = R.string.tip_factory_wifi_desc,
        iconRes = R.drawable.ic_qr_code_scanner
    ),
    Email(
        labelRes = R.string.label_factory_type_email,
        descriptionRes = R.string.tip_factory_email_desc,
        iconRes = R.drawable.ic_info
    ),
    Event(
        labelRes = R.string.label_factory_type_event,
        descriptionRes = R.string.tip_factory_event_desc,
        iconRes = R.drawable.ic_interests
    ),
    Phone(
        labelRes = R.string.label_factory_type_phone,
        descriptionRes = R.string.tip_factory_phone_desc,
        iconRes = R.drawable.ic_exit_to_app
    ),
    Message(
        labelRes = R.string.label_factory_type_message,
        descriptionRes = R.string.tip_factory_message_desc,
        iconRes = R.drawable.ic_wechat
    ),
    Geo(
        labelRes = R.string.label_factory_type_geo,
        descriptionRes = R.string.tip_factory_geo_desc,
        iconRes = R.drawable.ic_link
    ),
    Application(
        labelRes = R.string.label_factory_type_application,
        descriptionRes = R.string.tip_factory_app_desc,
        iconRes = R.drawable.ic_code
    ),
}