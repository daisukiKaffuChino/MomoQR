package github.daisukikaffuchino.momoqr

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import github.daisukikaffuchino.momoqr.logic.database.StarDatabase
import github.daisukikaffuchino.momoqr.ui.pages.crash.CrashHandler

@HiltAndroidApp
class MomoApplication : Application() {
    private val database by lazy { StarDatabase.getDatabase(this) }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        lateinit var db: StarDatabase
    }

    override fun onCreate() {
        super.onCreate()

        val crashHandler = CrashHandler(applicationContext)
        Thread.setDefaultUncaughtExceptionHandler(crashHandler)

        db = database
        context = applicationContext
    }
}