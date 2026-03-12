package github.daisukikaffuchino.momoqr.ui.pages.result.state

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import github.daisukikaffuchino.momoqr.MomoApplication
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.logic.database.StarEntity
import github.daisukikaffuchino.momoqr.logic.model.QRCodeECL

class ResultState(val initialStar: StarEntity? = null) {
    var qrContent by mutableStateOf(initialStar?.content ?: "")
    var isErrorContent by mutableStateOf(false)
    var selectedCategoryIndex by mutableIntStateOf(-2)
    var categoryContent by mutableStateOf(initialStar?.category ?: "")
    var isErrorCategory by mutableStateOf(false)
    var isMarked by mutableStateOf(initialStar?.marked == true)
    var ecl by mutableFloatStateOf(initialStar?.errorCorrectionLevel ?: 15f)

    var categorySupportingText by mutableIntStateOf(R.string.tip_short_category)
        private set

    var showExitConfirmDialog by mutableStateOf(false)
    var showDeleteConfirmDialog by mutableStateOf(false)

    fun setErrorIfNotValid(): Boolean {
        val categoryMaxLength = 20
        val qr = qrContent.trim()
        val qrMaxBytes = QRCodeECL.fromFloat(ecl).getQrMaxBytes()
        val qrBytesLength = qr.toByteArray(Charsets.UTF_8).size
        val category = categoryContent.trim()

        isErrorContent = when {
            qr.isEmpty() -> true
            qrBytesLength > qrMaxBytes -> true
            else -> false
        }

        if (selectedCategoryIndex == -1) {
            isErrorCategory = when {
                category.isEmpty() -> true
                category.length > categoryMaxLength -> true
                else -> false
            }

            categorySupportingText = when {
                category.isEmpty() -> R.string.error_no_content_entered
                category.length > categoryMaxLength -> R.string.error_category_too_long
                else -> R.string.tip_short_category
            }
        } else {
            isErrorCategory = false
        }

        return isErrorContent || isErrorCategory
    }

    fun clearError() {
        isErrorContent = false
        isErrorCategory = false
    }

    fun isModified(): Boolean {
        var isModified = false
        if ((initialStar?.content ?: "") != qrContent) isModified = true
        if ((initialStar?.category ?: "") != categoryContent) isModified = true
        if ((initialStar?.marked == true) != isMarked) isModified = true
        return isModified
    }


    object Saver : androidx.compose.runtime.saveable.Saver<ResultState, Any> {
        override fun SaverScope.save(value: ResultState): Any {
            return listOf(
                value.initialStar?.let {
                    listOf(
                        it.title,
                        it.content,
                        it.category,
                        it.marked,
                        it.imgPath,
                        it.errorCorrectionLevel,
                        it.date,
                        it.id
                    )
                },
                value.qrContent,
                value.isErrorContent,
                value.selectedCategoryIndex,
                value.categoryContent,
                value.isErrorCategory,
                value.isMarked,
                value.ecl,
                value.showExitConfirmDialog,
                value.showDeleteConfirmDialog
            )
        }

        override fun restore(value: Any): ResultState {
            val list = value as List<*>
            val initialList = list[0] as? List<*>
            val initial = initialList?.let {
                StarEntity(
                    title = it[0] as String,
                    content = it[1] as String,
                    category = it[2] as String,
                    marked = it[3] as Boolean,
                    imgPath = it[4] as String,
                    errorCorrectionLevel = it[5] as Float,
                    date = it[6] as Long,
                    id = it[7] as Int
                )
            }
            return ResultState(initial).apply {
                qrContent = list[1] as String
                isErrorContent = list[2] as Boolean
                selectedCategoryIndex = list[3] as Int
                categoryContent = list[4] as String
                isErrorCategory = list[5] as Boolean
                isMarked = list[6] as Boolean
                ecl = list[7] as Float
                showExitConfirmDialog = list[8] as Boolean
                showDeleteConfirmDialog = list[9] as Boolean
            }
        }
    }
}

@Composable
fun rememberResultState(initialData: StarEntity? = null): ResultState =
    rememberSaveable(saver = ResultState.Saver) { ResultState(initialData) }