package github.daisukikaffuchino.momoqr.logic.database

import github.daisukikaffuchino.momoqr.MomoApplication
import kotlinx.coroutines.flow.Flow

object Repository : IRepository {
    private val database get() = MomoApplication.db
    private val starDao = database.starDao()

    override suspend fun insertStar(star: StarEntity) {
        starDao.insert(star)
    }

    override fun getAllStars(): Flow<List<StarEntity>> = starDao.getAll()

    override suspend fun updateStar(star: StarEntity) {
        starDao.update(star)
    }

    override suspend fun deleteStar(star: StarEntity) {
        starDao.delete(star)
    }

    override suspend fun deleteStarFromIds(stars: List<Int>) {
        starDao.deleteFromIds(stars.toSet())
    }
}