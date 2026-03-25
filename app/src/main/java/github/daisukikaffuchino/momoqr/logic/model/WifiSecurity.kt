package github.daisukikaffuchino.momoqr.logic.model

import github.daisukikaffuchino.momoqr.R

enum class WifiSecurity(
    val labelRes: Int,
    val protocol: String,
) {
    WPA(labelRes = R.string.label_factory_wifi_security_wpa, protocol = "WPA"),
    WPA3(labelRes = R.string.label_factory_wifi_security_wpa3, protocol = "SAE"),
    WEP(labelRes = R.string.label_factory_wifi_security_wep, protocol = "WEP"),
    None(labelRes = R.string.none, protocol = "nopass"),
}