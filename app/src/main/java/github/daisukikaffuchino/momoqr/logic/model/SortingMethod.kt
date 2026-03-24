package github.daisukikaffuchino.momoqr.logic.model

import androidx.annotation.StringRes
import github.daisukikaffuchino.momoqr.R

enum class SortingMethod(
    val id: Int,
    @param:StringRes val nameRes: Int
) {
    Sequential(id = 1, nameRes = R.string.sorting_sequential),
    ModifiedDate(id = 2, nameRes = R.string.sorting_modified_date),
    Category(id = 3, nameRes = R.string.sorting_category),
    AlphabeticalAscending(id = 4, nameRes = R.string.sorting_alphabetical_ascending),
    AlphabeticalDescending(id = 5, nameRes = R.string.sorting_alphabetical_descending);

    companion object {
        fun fromId(id: Int) = entries.find { it.id == id } ?: Sequential
    }
}
