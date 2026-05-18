package com.luigi.lifeos.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.luigi.lifeos.core.database.entity.RecipeEntity
import com.luigi.lifeos.core.database.entity.RecipeIngredientEntity
import com.luigi.lifeos.core.database.relation.RecipeWithIngredients
import kotlinx.coroutines.flow.Flow

@Dao
abstract class RecipeDao {

    @Transaction
    @Query("SELECT * FROM recipes ORDER BY name ASC")
    abstract fun observeAllWithIngredients(): Flow<List<RecipeWithIngredients>>

    @Transaction
    @Query("SELECT * FROM recipes WHERE id = :id")
    abstract suspend fun getWithIngredients(id: Long): RecipeWithIngredients?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertRecipe(recipe: RecipeEntity): Long

    @Update
    abstract suspend fun updateRecipe(recipe: RecipeEntity): Int

    @Delete
    abstract suspend fun deleteRecipe(recipe: RecipeEntity): Int

    // ── RecipeIngredient lines ────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertIngredientLine(line: RecipeIngredientEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertIngredientLines(lines: List<RecipeIngredientEntity>): List<Long>

    @Query("DELETE FROM recipe_ingredients WHERE recipe_id = :recipeId")
    abstract suspend fun deleteIngredientLines(recipeId: Long): Int

    /** Replace all ingredient lines for a recipe atomically. */
    @Transaction
    open suspend fun replaceIngredientLines(recipeId: Long, lines: List<RecipeIngredientEntity>): Int {
        val deletedCount = deleteIngredientLines(recipeId)
        insertIngredientLines(lines)
        return deletedCount
    }

    /**
     * Insert (or replace) a recipe and its ingredient lines in a single transaction.
     * Returns the stored recipe ID.
     *
     * Use this instead of calling [insertRecipe] + [replaceIngredientLines] separately
     * — keeps the write atomic so a partial crash can't leave a recipe without ingredients.
     */
    @Transaction
    open suspend fun upsertRecipeWithIngredients(
        recipe: RecipeEntity,
        lines: List<RecipeIngredientEntity>,
    ): Long {
        val recipeId = insertRecipe(recipe)
        // Reassign recipeId on each line — critical for new recipes where recipe.id was 0
        // and insertRecipe returned the auto-generated id.
        val linesWithCorrectId = lines.map { it.copy(recipeId = recipeId) }
        replaceIngredientLines(recipeId, linesWithCorrectId)
        return recipeId
    }
}
