package github.daisukikaffuchino.momoqr.logic.model

enum class ErrorCorrectionLevel(
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
}