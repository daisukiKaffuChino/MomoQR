package github.daisukikaffuchino.momoqr.logic.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import github.daisukikaffuchino.momoqr.constants.Constants
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = Constants.DB_TABLE_NAME)
data class StarEntity(
    @ColumnInfo(name = "title") val title: String = "",
    @ColumnInfo(name = "content") val content: String = "",
    @ColumnInfo(name = "category") val category: String = "",
    @ColumnInfo(name = "marked") val marked: Boolean = false,
    @ColumnInfo(name = "imgPath") val imgPath: String = "",
    @ColumnInfo(name = "date") val date: Long = 0,
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int = 0,
)
