package com.luigi.lifeos.core.database.di

import android.content.Context
import androidx.room.Room
import com.luigi.lifeos.core.database.LifeOsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Single Room instance per process. Encrypted-at-rest will arrive later
     * via SQLCipher (see Security goals in spec) — wire in via
     * `openHelperFactory(SupportFactory(passphrase))`.
     */
    @Provides
    @Singleton
    fun provideLifeOsDatabase(@ApplicationContext context: Context): LifeOsDatabase =
        Room.databaseBuilder(context, LifeOsDatabase::class.java, "lifeos.db")
            // DO NOT call fallbackToDestructiveMigration() — see LifeOsDatabase kdoc.
            .build()
}
