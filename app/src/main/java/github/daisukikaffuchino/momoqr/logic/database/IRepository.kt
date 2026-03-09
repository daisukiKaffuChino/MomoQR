package github.daisukikaffuchino.momoqr.logic.database

import kotlinx.coroutines.flow.Flow

interface IRepository {
    suspend fun insertStar(star: StarEntity)

    fun getAllStars(): Flow<List<StarEntity>>

    suspend fun updateStar(star: StarEntity)

    suspend fun deleteStar(star: StarEntity)

    suspend fun deleteStarFromIds(stars: List<Int>)
}