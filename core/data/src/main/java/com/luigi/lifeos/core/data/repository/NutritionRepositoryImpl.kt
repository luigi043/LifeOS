package com.luigi.lifeos.core.data.repository

import com.luigi.lifeos.core.database.dao.IngredientDao
import com.luigi.lifeos.core.database.dao.RecipeDao
import com.luigi.lifeos.core.database.entity.IngredientEntity
import com.luigi.lifeos.core.database.entity.RecipeEntity
import com.luigi.lifeos.core.database.entity.RecipeIngredientEntity
import com.luigi.lifeos.core.domain.model.Ingredient
import com.luigi.lifeos.core.domain.model.Recipe
import com.luigi.lifeos.core.domain.repository.NutritionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NutritionRepositoryImpl @Inject constructor(
    private val ingredientDao: IngredientDao,
    private val recipeDao: RecipeDao,
) : NutritionRepository {

    // ── Ingredients ──────────────────────────────────────────────────────────

    override fun observeIngredients(): Flow<List<Ingredient>> =
        ingredientDao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun getIngredient(id: Long): Ingredient? =
        ingredientDao.getById(id)?.toDomain()

    override suspend fun upsertIngredient(ingredient: Ingredient): Long =
        ingredientDao.insert(IngredientEntity.fromDomain(ingredient))

    override suspend fun updateIngredientPrice(id: Long, pricePerUnit: Double) {
        ingredientDao.updatePrice(id, pricePerUnit)
    }

    override suspend fun deleteIngredient(ingredient: Ingredient) {
        ingredientDao.delete(IngredientEntity.fromDomain(ingredient))
    }

    // ── Recipes ──────────────────────────────────────────────────────────────

    override fun observeRecipes(): Flow<List<Recipe>> =
        recipeDao.observeAllWithIngredients().map { list -> list.map { it.toDomain() } }

    override suspend fun getRecipe(id: Long): Recipe? =
        recipeDao.getWithIngredients(id)?.toDomain()

    override suspend fun upsertRecipe(recipe: Recipe): Long {
        val recipeEntity = RecipeEntity(
            id = recipe.id,
            name = recipe.name,
            servings = recipe.servings,
            prepTimeMinutes = recipe.prepTimeMinutes,
            cookTimeMinutes = recipe.cookTimeMinutes,
            notes = recipe.notes,
        )
        // recipeId = 0L is a placeholder — upsertRecipeWithIngredients replaces it
        // with the actual id returned by Room after the INSERT.
        val lines = recipe.ingredients.map { ri ->
            RecipeIngredientEntity(
                recipeId = 0L,
                ingredientId = ri.ingredientId,
                quantity = ri.quantity,
                unit = ri.unit,
            )
        }
        return recipeDao.upsertRecipeWithIngredients(recipeEntity, lines)
    }

    override suspend fun deleteRecipe(recipe: Recipe) {
        recipeDao.deleteRecipe(
            RecipeEntity(
                id = recipe.id,
                name = recipe.name,
                servings = recipe.servings,
                prepTimeMinutes = recipe.prepTimeMinutes,
                cookTimeMinutes = recipe.cookTimeMinutes,
                notes = recipe.notes,
            ),
        )
    }
}
