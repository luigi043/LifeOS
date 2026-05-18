package com.luigi.lifeos.core.domain.model

/**
 * A recipe is a named collection of [RecipeIngredient]s with a serving count.
 *
 * This lives in :core:domain (pure Kotlin) so both :feature:nutrition and
 * :core:data can depend on it without Android imports.
 */
data class Recipe(
    val id: Long = 0L,
    val name: String,
    val servings: Int = 1,
    val prepTimeMinutes: Int = 0,
    val cookTimeMinutes: Int = 0,
    val notes: String = "",
    val ingredients: List<RecipeIngredient> = emptyList(),
)

/**
 * A single ingredient line inside a recipe, e.g. "200 g chicken breast".
 * [quantity] is in the ingredient's native [Ingredient.unit].
 */
data class RecipeIngredient(
    val ingredientId: Long,
    val ingredientName: String,   // denormalised for display — avoids extra lookups
    val quantity: Double,
    val unit: String,
    val estimatedPricePerUnit: Double? = null,
) {
    /** Cost of this line, null if price is unknown. */
    val lineCost: Double? get() = estimatedPricePerUnit?.let { it * quantity }
}
