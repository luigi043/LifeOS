package com.luigi.lifeos

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

/**
 * Hilt application root + WorkManager bootstrap.
 *
 * Implements [Configuration.Provider] so WorkManager picks up the
 * [HiltWorkerFactory] (required for `@HiltWorker`-annotated workers in
 * `:notifications` and `:sync`). The default initializer is removed in
 * the manifest so on-demand initialization is used here.
 */
@HiltAndroidApp
class LifeOsApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        // TODO: plant a release-safe tree (e.g. CrashlyticsTree) in non-debug.
        Timber.plant(Timber.DebugTree())
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
}
