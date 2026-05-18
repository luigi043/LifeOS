package com.luigi.lifeos.core.domain.model

/**
 * A single line on the grocery list.
 *
 * Items can be:
 *  - auto-generated from a meal plan (has [sourceRecipeId])
 *  - manually added by the user ([sourceRecipeId] == null)
 *
 * [estimatedPrice] = [estimatedPricePerUnit] * [quantity]. Kept as a nullable
 * Double so the budget warning system can skip items with no price set.
 */
data class GroceryItem(
    val id: Long = 0L,
    val name: String,
    val quantity: Double,
    val unit: String,
    val estimatedPricePerUnit: Double? = null,
    val isPurchased: Boolean = false,
    val category: IngredientCategory = IngredientCategory.OTHER,
    val sourceRecipeId: Long? = null,   // which recipe generated this item, if any
) {
    val estimatedPrice: Double? get() = estimatedPricePerUnit?.let { it * quantity }
}
