package com.luigi.lifeos.core.data.di

import com.luigi.lifeos.core.domain.usecase.CalculateMealCostUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Provides pure-domain use cases that have no Android dependencies.
 * Keeping them here (rather than in feature modules) allows multiple features
 * to share them without re-declaring.
 */
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideCalculateMealCostUseCase(): CalculateMealCostUseCase = CalculateMealCostUseCase()
}
