package github.daisukikaffuchino.momoqr.logic.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import github.daisukikaffuchino.momoqr.constants.AppConstants
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = AppConstants.DB_TABLE_NAME)
data class StarEntity(
    @ColumnInfo(name = "title") val title: String = "",
    @ColumnInfo(name = "content") val content: String = "",
    @ColumnInfo(name = "category") val category: String = "",
    @ColumnInfo(name = "marked") val marked: Boolean = false,
    @ColumnInfo(name = "imgPath") val imgPath: String = "",
    @ColumnInfo(name = "errorCorrectionLevel") val errorCorrectionLevel: Float = 15f,
    @ColumnInfo(name = "modifiedDate") val modifiedDate: Long = 0,
    @ColumnInfo(name = "createdDate") val createdDate: Long = 0,
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int = 0,
)
