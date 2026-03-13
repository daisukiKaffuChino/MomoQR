package github.daisukikaffuchino.momoqr.logic.model

enum class SearchEngine(
    val value: String,
    val label: String
) {
    GOOGLE("google", "Google"),
    BING("bing", "Bing"),
    YANDEX("yandex", "Yandex"),
    BAIDU("baidu", "Baidu");

    companion object {
        fun fromValue(value: String): SearchEngine {
            return entries.find { it.value == value } ?: GOOGLE
        }
    }
}