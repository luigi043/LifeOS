package com.luigi.lifeos.core.domain.usecase

import com.luigi.lifeos.core.domain.model.MealCost
import com.luigi.lifeos.core.domain.model.Recipe

/**
 * Pure domain use case — no Android, no DI, fully unit-testable.
 *
 * Given a [Recipe] (with ingredients that carry their estimated prices),
 * returns a [MealCost] that breaks down cost per ingredient line, flags
 * missing prices, and computes cost-per-serving.
 *
 * Why here (not in a feature module): cost calculation is a cross-cutting
 * concern used by both :feature:nutrition (recipe detail) and :feature:grocery
 * (budget warnings). Keeping it in :core:domain avoids circular dependencies.
 *
 * Usage:
 * ```
 * val useCase = CalculateMealCostUseCase()
 * val cost = useCase(recipe)
 * ```
 */
class CalculateMealCostUseCase {

    operator fun invoke(recipe: Recipe): MealCost {
        val breakdown = mutableListOf<MealCost.CostLine>()
        val missingPrices = mutableListOf<String>()

        for (ingredient in recipe.ingredients) {
            val price = ingredient.estimatedPricePerUnit
            if (price == null) {
                missingPrices += ingredient.ingredientName
            } else {
                breakdown += MealCost.CostLine(
                    ingredientName = ingredient.ingredientName,
                    quantity = ingredient.quantity,
                    unit = ingredient.unit,
                    lineCost = price * ingredient.quantity,
                )
            }
        }

        return MealCost(
            recipeId = recipe.id,
            recipeName = recipe.name,
            servings = recipe.servings,
            breakdown = breakdown,
            missingPrices = missingPrices,
        )
    }
}
