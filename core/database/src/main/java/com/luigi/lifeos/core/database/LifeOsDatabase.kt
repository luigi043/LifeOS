package com.luigi.lifeos.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.luigi.lifeos.core.database.converter.EnumConverters
import com.luigi.lifeos.core.database.converter.InstantConverters
import com.luigi.lifeos.core.database.dao.BudgetDao
import com.luigi.lifeos.core.database.dao.ExpenseCategoryDao
import com.luigi.lifeos.core.database.dao.ExpenseDao
import com.luigi.lifeos.core.database.dao.GroceryItemDao
import com.luigi.lifeos.core.database.dao.IncomeDao
import com.luigi.lifeos.core.database.dao.IngredientDao
import com.luigi.lifeos.core.database.dao.RecipeDao
import com.luigi.lifeos.core.database.dao.UserProfileDao
import com.luigi.lifeos.core.database.entity.BudgetEntity
import com.luigi.lifeos.core.database.entity.ExpenseCategoryEntity
import com.luigi.lifeos.core.database.entity.ExpenseEntity
import com.luigi.lifeos.core.database.entity.GroceryItemEntity
import com.luigi.lifeos.core.database.entity.IncomeEntity
import com.luigi.lifeos.core.database.entity.IngredientEntity
import com.luigi.lifeos.core.database.entity.RecipeEntity
import com.luigi.lifeos.core.database.entity.RecipeIngredientEntity
import com.luigi.lifeos.core.database.entity.UserProfileEntity
import com.luigi.lifeos.core.database.migration.MIGRATION_1_2
import com.luigi.lifeos.core.database.migration.MIGRATION_2_3

/**
 * Unified Room database for LifeOS.
 *
 * MIGRATION POLICY: every schema change MUST ship a [androidx.room.migration.Migration].
 * NO `fallbackToDestructiveMigration()` in production. Schemas are exported to
 * `core/database/schemas/` and committed to git so Room can auto-validate
 * migrations in tests.
 *
 * Version history:
 *   v1 → UserProfile only (Phase 1 foundation)
 *   v2 → Finance: expense_categories, expenses, income, budgets
 *   v3 → Nutrition + Grocery: ingredients, recipes, recipe_ingredients, grocery_items
 */
@Database(
    entities = [
        // core
        UserProfileEntity::class,
        // finance
        ExpenseCategoryEntity::class,
        ExpenseEntity::class,
        IncomeEntity::class,
        BudgetEntity::class,
        // nutrition
        IngredientEntity::class,
        RecipeEntity::class,
        RecipeIngredientEntity::class,
        // grocery
        GroceryItemEntity::class,
    ],
    version = 3,
    exportSchema = true,
)
@TypeConverters(InstantConverters::class, EnumConverters::class)
abstract class LifeOsDatabase : RoomDatabase() {

    // ── Core ────────────────────────────────────────────────────────────────
    abstract fun userProfileDao(): UserProfileDao

    // ── Finance ─────────────────────────────────────────────────────────────
    abstract fun expenseCategoryDao(): ExpenseCategoryDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun incomeDao(): IncomeDao
    abstract fun budgetDao(): BudgetDao

    // ── Nutrition ────────────────────────────────────────────────────────────
    abstract fun ingredientDao(): IngredientDao
    abstract fun recipeDao(): RecipeDao

    // ── Grocery ──────────────────────────────────────────────────────────────
    abstract fun groceryItemDao(): GroceryItemDao

    companion object {
        /** All migrations in order. Pass to [RoomDatabase.Builder.addMigrations]. */
        val ALL_MIGRATIONS = arrayOf(MIGRATION_1_2, MIGRATION_2_3)
    }
}
