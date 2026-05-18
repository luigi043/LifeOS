package com.luigi.lifeos.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Join table: one row per ingredient line in a recipe.
 *
 * Foreign keys ensure referential integrity.
 * Cascade delete keeps the table clean when a recipe or ingredient is removed.
 */
@Entity(
    tableName = "recipe_ingredients",
    primaryKeys = ["recipe_id", "ingredient_id"],
    foreignKeys = [
        ForeignKey(
            entity = RecipeEntity::class,
            parentColumns = ["id"],
            childColumns = ["recipe_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = IngredientEntity::class,
            parentColumns = ["id"],
            childColumns = ["ingredient_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index("recipe_id"),
        Index("ingredient_id"),
    ],
)
data class RecipeIngredientEntity(
    @ColumnInfo(name = "recipe_id")
    val recipeId: Long,

    @ColumnInfo(name = "ingredient_id")
    val ingredientId: Long,

    @ColumnInfo(name = "quantity")
    val quantity: Double,

    @ColumnInfo(name = "unit")
    val unit: String,
)
