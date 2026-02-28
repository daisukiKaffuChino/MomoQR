package github.daisukikaffuchino.momoqr.logic.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import github.daisukikaffuchino.momoqr.constants.Constants

@Database(entities = [StarEntity::class], version = 1)
abstract class StarDatabase : RoomDatabase() {

    abstract fun starDao(): StarDao

    companion object {
        @Volatile
        private var INSTANCE: StarDatabase? = null
        fun getDatabase(context: Context): StarDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StarDatabase::class.java,
                    Constants.DB_NAME
                )
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }

}