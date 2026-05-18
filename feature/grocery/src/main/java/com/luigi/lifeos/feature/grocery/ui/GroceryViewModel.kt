package com.luigi.lifeos.feature.grocery.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luigi.lifeos.core.domain.model.GroceryItem
import com.luigi.lifeos.core.domain.repository.GroceryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroceryViewModel @Inject constructor(
    private val groceryRepository: GroceryRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(GroceryUiState())
    val state: StateFlow<GroceryUiState> = _state.asStateFlow()

    init {
        combine(
            groceryRepository.observeAll(),
            groceryRepository.observePendingEstimatedTotal(),
        ) { items, total ->
            _state.update {
                it.copy(
                    items = items,
                    pendingEstimatedTotal = total,
                    isLoading = false,
                )
            }
        }.launchIn(viewModelScope)
    }

    fun markPurchased(id: Long, purchased: Boolean) {
        viewModelScope.launch {
            groceryRepository.markPurchased(id, purchased)
        }
    }

    fun addItem(item: GroceryItem) {
        viewModelScope.launch {
            groceryRepository.addItem(item)
        }
    }

    fun deleteItem(item: GroceryItem) {
        viewModelScope.launch {
            groceryRepository.deleteItem(item)
        }
    }

    fun clearPurchased() {
        viewModelScope.launch {
            groceryRepository.clearPurchased()
        }
    }
}

data class GroceryUiState(
    val isLoading: Boolean = true,
    val items: List<GroceryItem> = emptyList(),
    val pendingEstimatedTotal: Double? = null,   // null = some items have no price
    val error: String? = null,
) {
    val pendingItems: List<GroceryItem> get() = items.filter { !it.isPurchased }
    val purchasedItems: List<GroceryItem> get() = items.filter { it.isPurchased }
}
