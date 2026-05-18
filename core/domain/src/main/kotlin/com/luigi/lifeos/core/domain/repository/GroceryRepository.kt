package com.luigi.lifeos.core.domain.repository

import com.luigi.lifeos.core.domain.model.GroceryItem
import kotlinx.coroutines.flow.Flow

interface GroceryRepository {

    fun observeAll(): Flow<List<GroceryItem>>

    fun observePending(): Flow<List<GroceryItem>>

    /** Live estimated total cost of all unpurchased items with a price set. */
    fun observePendingEstimatedTotal(): Flow<Double?>

    suspend fun addItem(item: GroceryItem): Long

    /** Generate grocery items from a recipe's ingredient list. */
    suspend fun addItemsFromRecipe(items: List<GroceryItem>)

    suspend fun markPurchased(id: Long, purchased: Boolean)

    suspend fun deleteItem(item: GroceryItem)

    suspend fun clearPurchased()
}
