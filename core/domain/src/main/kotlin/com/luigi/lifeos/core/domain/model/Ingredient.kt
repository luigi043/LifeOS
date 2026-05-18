package com.luigi.lifeos.core.domain.model

/**
 * A base ingredient that can appear in recipes and grocery lists.
 *
 * [estimatedPricePerUnit] is nullable — price is unknown until the user sets it
 * or we get it from a grocery API later (Phase 6).
 *
 * [unit] examples: "g", "ml", "unit", "tbsp", "cup"
 */
data class Ingredient(
    val id: Long = 0L,
    val name: String,
    val unit: String,
    val estimatedPricePerUnit: Double? = null, // € per unit
    val category: IngredientCategory = IngredientCategory.OTHER,
)

enum class IngredientCategory {
    PRODUCE,
    PROTEIN,
    DAIRY,
    GRAINS,
    CONDIMENTS,
    FROZEN,
    BEVERAGES,
    OTHER,
}
