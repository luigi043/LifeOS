package com.luigi.lifeos.feature.nutrition.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luigi.lifeos.core.domain.model.MealCost
import com.luigi.lifeos.core.domain.model.Recipe
import com.luigi.lifeos.core.domain.repository.NutritionRepository
import com.luigi.lifeos.core.domain.usecase.CalculateMealCostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NutritionViewModel @Inject constructor(
    private val nutritionRepository: NutritionRepository,
    private val calculateMealCost: CalculateMealCostUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(NutritionUiState())
    val state: StateFlow<NutritionUiState> = _state.asStateFlow()

    init {
        nutritionRepository.observeRecipes()
            .onEach { recipes ->
                val costs = recipes.map { calculateMealCost(it) }
                _state.update { it.copy(recipes = recipes, mealCosts = costs, isLoading = false) }
            }
            .launchIn(viewModelScope)
    }

    fun deleteRecipe(recipe: Recipe) {
        viewModelScope.launch {
            nutritionRepository.deleteRecipe(recipe)
        }
    }

    /** Recalculate cost for a single recipe — called after updating an ingredient price. */
    fun recalculateCost(recipe: Recipe) {
        val cost = calculateMealCost(recipe)
        _state.update { current ->
            val updated = current.mealCosts.map { if (it.recipeId == recipe.id) cost else it }
            current.copy(mealCosts = updated)
        }
    }
}

data class NutritionUiState(
    val isLoading: Boolean = true,
    val recipes: List<Recipe> = emptyList(),
    val mealCosts: List<MealCost> = emptyList(),
    val error: String? = null,
) {
    /** Convenience — find the pre-computed cost for a given recipe. */
    fun costFor(recipeId: Long): MealCost? = mealCosts.firstOrNull { it.recipeId == recipeId }
}
