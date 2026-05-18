package com.luigi.lifeos.core.datastore.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    // UserPreferences is provided via @Inject constructor and @Singleton annotation.
    // Additional DataStore-related providers (like the Proto DataStore instance) would go here.
}
