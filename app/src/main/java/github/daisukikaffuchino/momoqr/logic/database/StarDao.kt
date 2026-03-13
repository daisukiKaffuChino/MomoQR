package github.daisukikaffuchino.momoqr.logic.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import github.daisukikaffuchino.momoqr.constants.AppConstants

@Dao
interface StarDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(starItem: StarEntity)

    @Query("SELECT * FROM ${AppConstants.DB_TABLE_NAME}")
    fun getAll(): Flow<List<StarEntity>>

    @Update
    suspend fun update(starItem: StarEntity)

    @Delete
    suspend fun delete(starItem: StarEntity)

    @Query("DELETE FROM ${AppConstants.DB_TABLE_NAME} WHERE id in (:ids)")
    suspend fun deleteFromIds(ids: Set<Int>)

    @Query("DELETE FROM ${AppConstants.DB_TABLE_NAME}")
    suspend fun deleteAllStars()
}