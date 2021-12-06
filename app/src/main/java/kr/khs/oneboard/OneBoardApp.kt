package kr.khs.oneboard

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class OneBoardApp : Application() {

    override fun onCreate() {
        super.onCreate()

        application = this

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    // todo refactor later
    companion object {
        var application: Application? = null

        fun getInstance(): Context {
            return application!!
        }
    }
}