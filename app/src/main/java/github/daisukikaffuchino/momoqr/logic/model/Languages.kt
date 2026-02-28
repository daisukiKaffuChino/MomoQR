package github.daisukikaffuchino.momoqr.logic.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class Languages(
    val language: Language = Language.SYSTEM
) {
    @Serializable
    enum class Language(val code: String?) {
        SYSTEM(null),

        ENGLISH("en"),
        CHINESE_SIMPLIFIED("zh-CN"),
        CHINESE_TRADITIONAL("zh-TW")
    }
}
