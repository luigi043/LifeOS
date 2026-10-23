package com.luigi.lifeos

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Hilt application root.
 */
@HiltAndroidApp
class LifeOsApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // TODO: plant a release-safe tree (e.g. CrashlyticsTree) in non-debug.
        Timber.plant(Timber.DebugTree())
    }
}
