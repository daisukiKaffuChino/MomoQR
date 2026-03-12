package github.daisukikaffuchino.momoqr.logic.model

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

enum class QRCodeECL(
    val value: Float,
    val nameString: String
) {
    Seven(value = 7f, nameString = "~7%"),
    Fifteen(value = 15f, nameString = "~15%"),
    TWENTY_FIVE(value = 25f, nameString = "~25%"),
    THIRTY(value = 30f, nameString = "~30%");

    companion object {
        fun fromFloat(float: Float) = entries.find { it.value == float } ?: Fifteen
    }

    fun toZXingLevel(): ErrorCorrectionLevel = when (this) {
        Seven -> ErrorCorrectionLevel.L
        Fifteen -> ErrorCorrectionLevel.M
        TWENTY_FIVE -> ErrorCorrectionLevel.Q
        THIRTY -> ErrorCorrectionLevel.H
    }

    fun toDisplayString(): String = when (this) {
        Seven -> "L (~7%)"
        Fifteen -> "M (~15%)"
        TWENTY_FIVE -> "Q (~25%)"
        THIRTY -> "H (~30%)"
    }

    fun getQrMaxBytes(): Int = when (this) {
        Seven -> 2900
        Fifteen -> 2300
        TWENTY_FIVE -> 1600
        THIRTY -> 1200
    }
}