package com.luigi.lifeos.core.database.relation

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.luigi.lifeos.core.database.entity.IngredientEntity
import com.luigi.lifeos.core.database.entity.RecipeEntity
import com.luigi.lifeos.core.database.entity.RecipeIngredientEntity
import com.luigi.lifeos.core.domain.model.Recipe
import com.luigi.lifeos.core.domain.model.RecipeIngredient

/**
 * Room @Relation that loads a recipe together with all its ingredient lines
 * in a single query. Used by [RecipeDao.observeAllWithIngredients] and
 * [RecipeDao.getWithIngredients].
 */
data class RecipeWithIngredients(
    @Embedded
    val recipe: RecipeEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = RecipeIngredientEntity::class,
            parentColumn = "recipe_id",
            entityColumn = "ingredient_id",
        ),
    )
    val ingredients: List<IngredientEntity>,

    // Raw join rows — needed to read quantity/unit per line
    @Relation(
        parentColumn = "id",
        entityColumn = "recipe_id",
    )
    val ingredientLines: List<RecipeIngredientEntity>,
) {
    fun toDomain(): Recipe {
        val ingredientById = ingredients.associateBy { it.id }

        val recipeIngredients = ingredientLines.mapNotNull { line ->
            val ingredient = ingredientById[line.ingredientId] ?: return@mapNotNull null
            RecipeIngredient(
                ingredientId = ingredient.id,
                ingredientName = ingredient.name,
                quantity = line.quantity,
                unit = line.unit,
                estimatedPricePerUnit = ingredient.estimatedPricePerUnit,
            )
        }

        return Recipe(
            id = recipe.id,
            name = recipe.name,
            servings = recipe.servings,
            prepTimeMinutes = recipe.prepTimeMinutes,
            cookTimeMinutes = recipe.cookTimeMinutes,
            notes = recipe.notes,
            ingredients = recipeIngredients,
        )
    }
}
