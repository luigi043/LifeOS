package com.luigi.lifeos.core.data.repository

import com.luigi.lifeos.core.database.dao.GroceryItemDao
import com.luigi.lifeos.core.database.entity.GroceryItemEntity
import com.luigi.lifeos.core.domain.model.GroceryItem
import com.luigi.lifeos.core.domain.repository.GroceryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroceryRepositoryImpl @Inject constructor(
    private val groceryItemDao: GroceryItemDao,
) : GroceryRepository {

    override fun observeAll(): Flow<List<GroceryItem>> =
        groceryItemDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observePending(): Flow<List<GroceryItem>> =
        groceryItemDao.observePending().map { list -> list.map { it.toDomain() } }

    override fun observePendingEstimatedTotal(): Flow<Double?> =
        groceryItemDao.observePendingEstimatedTotal()

    override suspend fun addItem(item: GroceryItem): Long =
        groceryItemDao.insert(GroceryItemEntity.fromDomain(item))

    override suspend fun addItemsFromRecipe(items: List<GroceryItem>) {
        groceryItemDao.insertAll(items.map { GroceryItemEntity.fromDomain(it) })
    }

    override suspend fun markPurchased(id: Long, purchased: Boolean) {
        groceryItemDao.markPurchased(id, purchased)
    }

    override suspend fun deleteItem(item: GroceryItem) {
        groceryItemDao.delete(GroceryItemEntity.fromDomain(item))
    }

    override suspend fun clearPurchased() {
        groceryItemDao.clearPurchased()
    }
}
