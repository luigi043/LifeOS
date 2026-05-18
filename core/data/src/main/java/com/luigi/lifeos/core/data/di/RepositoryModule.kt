package com.luigi.lifeos.core.data.di

import com.luigi.lifeos.core.data.repository.FinanceRepositoryImpl
import com.luigi.lifeos.core.data.repository.GroceryRepositoryImpl
import com.luigi.lifeos.core.data.repository.NutritionRepositoryImpl
import com.luigi.lifeos.core.data.repository.UserProfileRepositoryImpl
import com.luigi.lifeos.core.domain.repository.FinanceRepository
import com.luigi.lifeos.core.domain.repository.GroceryRepository
import com.luigi.lifeos.core.domain.repository.NutritionRepository
import com.luigi.lifeos.core.domain.repository.UserProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Binds interface → impl at the data layer boundary. Feature modules
 * inject *only* the interfaces, which means they never see Room/Retrofit
 * types and can be unit-tested with fakes.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserProfileRepository(
        impl: UserProfileRepositoryImpl,
    ): UserProfileRepository

    @Binds
    @Singleton
    abstract fun bindFinanceRepository(
        impl: FinanceRepositoryImpl,
    ): FinanceRepository

    @Binds
    @Singleton
    abstract fun bindNutritionRepository(
        impl: NutritionRepositoryImpl,
    ): NutritionRepository

    @Binds
    @Singleton
    abstract fun bindGroceryRepository(
        impl: GroceryRepositoryImpl,
    ): GroceryRepository
}
