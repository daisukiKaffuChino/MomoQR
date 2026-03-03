package github.daisukikaffuchino.momoqr.ui.navigation

import androidx.navigation3.runtime.NavKey
import github.daisukikaffuchino.momoqr.logic.database.StarEntity
import kotlinx.serialization.Serializable

@Serializable
sealed class MomoScreen : NavKey {
    @Serializable
    data object Home : MomoScreen()

    @Serializable
    data object Stars : MomoScreen()

    @Serializable
    sealed class Settings : MomoScreen() {
        @Serializable
        data object Main : Settings()

        @Serializable
        data object Appearance : Settings()

        @Serializable
        data object Interface : Settings()

        @Serializable
        data object Camera : Settings()

        @Serializable
        data object Data : Settings()

        @Serializable
        data object DataCategory : Settings()

        @Serializable
        data object Lab : Settings()

        @Serializable
        data object About : Settings()

        // @Serializable
        // data object AboutEasterEgg : Settings()

        @Serializable
        data object AboutLicence : Settings()
}

    @Serializable
    sealed class Result : MomoScreen() {
        @Serializable
        data class EditResult(val toDo: StarEntity) : Result()
    }


}