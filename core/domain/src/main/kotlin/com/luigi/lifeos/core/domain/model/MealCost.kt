package com.luigi.lifeos.core.domain.model

/**
 * Result of calculating the cost of a [Recipe].
 *
 * [totalCost] is null when at least one ingredient has no price set.
 * [costPerServing] = totalCost / servings.
 * [breakdown] lets the UI show which ingredients drive the cost.
 * [missingPrices] lists ingredient names the user still needs to fill in.
 */
data class MealCost(
    val recipeId: Long,
    val recipeName: String,
    val servings: Int,
    val breakdown: List<CostLine>,
    val missingPrices: List<String>,      // ingredient names without a price
) {
    val totalCost: Double? =
        if (missingPrices.isNotEmpty()) null
        else breakdown.sumOf { it.lineCost }

    val costPerServing: Double? =
        if (servings > 0) totalCost?.div(servings) else null

    val isComplete: Boolean get() = missingPrices.isEmpty()

    data class CostLine(
        val ingredientName: String,
        val quantity: Double,
        val unit: String,
        val lineCost: Double,             // always known — only in breakdown when price exists
    )
}
