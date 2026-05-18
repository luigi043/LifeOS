package com.luigi.lifeos.core.database.di

import android.content.Context
import androidx.room.Room
import com.luigi.lifeos.core.database.LifeOsDatabase
import com.luigi.lifeos.core.database.dao.BudgetDao
import com.luigi.lifeos.core.database.dao.ExpenseCategoryDao
import com.luigi.lifeos.core.database.dao.ExpenseDao
import com.luigi.lifeos.core.database.dao.GroceryItemDao
import com.luigi.lifeos.core.database.dao.IncomeDao
import com.luigi.lifeos.core.database.dao.IngredientDao
import com.luigi.lifeos.core.database.dao.RecipeDao
import com.luigi.lifeos.core.database.dao.UserProfileDao
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
     * via SQLCipher — wire in via `openHelperFactory(SupportFactory(passphrase))`.
     */
    @Provides
    @Singleton
    fun provideLifeOsDatabase(@ApplicationContext context: Context): LifeOsDatabase =
        Room.databaseBuilder(context, LifeOsDatabase::class.java, "lifeos.db")
            .addMigrations(*LifeOsDatabase.ALL_MIGRATIONS)
            // DO NOT call fallbackToDestructiveMigration() in production.
            .build()

    // ── Core ────────────────────────────────────────────────────────────────

    @Provides
    fun provideUserProfileDao(db: LifeOsDatabase): UserProfileDao = db.userProfileDao()

    // ── Finance ─────────────────────────────────────────────────────────────

    @Provides
    fun provideExpenseCategoryDao(db: LifeOsDatabase): ExpenseCategoryDao =
        db.expenseCategoryDao()

    @Provides
    fun provideExpenseDao(db: LifeOsDatabase): ExpenseDao = db.expenseDao()

    @Provides
    fun provideIncomeDao(db: LifeOsDatabase): IncomeDao = db.incomeDao()

    @Provides
    fun provideBudgetDao(db: LifeOsDatabase): BudgetDao = db.budgetDao()

    // ── Nutrition ────────────────────────────────────────────────────────────

    @Provides
    fun provideIngredientDao(db: LifeOsDatabase): IngredientDao = db.ingredientDao()

    @Provides
    fun provideRecipeDao(db: LifeOsDatabase): RecipeDao = db.recipeDao()

    // ── Grocery ──────────────────────────────────────────────────────────────

    @Provides
    fun provideGroceryItemDao(db: LifeOsDatabase): GroceryItemDao = db.groceryItemDao()
}
