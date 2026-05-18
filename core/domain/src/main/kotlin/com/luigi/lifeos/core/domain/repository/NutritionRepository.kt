package com.luigi.lifeos.core.domain.repository

import com.luigi.lifeos.core.domain.model.Ingredient
import com.luigi.lifeos.core.domain.model.Recipe
import kotlinx.coroutines.flow.Flow

interface NutritionRepository {

    // ── Ingredients ──────────────────────────────────────────────────────────

    fun observeIngredients(): Flow<List<Ingredient>>

    suspend fun getIngredient(id: Long): Ingredient?

    suspend fun upsertIngredient(ingredient: Ingredient): Long

    suspend fun updateIngredientPrice(id: Long, pricePerUnit: Double)

    suspend fun deleteIngredient(ingredient: Ingredient)

    // ── Recipes ──────────────────────────────────────────────────────────────

    fun observeRecipes(): Flow<List<Recipe>>

    suspend fun getRecipe(id: Long): Recipe?

    /**
     * Save (or replace) a full recipe including its ingredient lines.
     * Returns the stored recipe ID.
     */
    suspend fun upsertRecipe(recipe: Recipe): Long

    suspend fun deleteRecipe(recipe: Recipe)
}
