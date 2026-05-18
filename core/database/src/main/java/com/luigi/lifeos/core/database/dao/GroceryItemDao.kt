package com.luigi.lifeos.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.luigi.lifeos.core.database.entity.GroceryItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GroceryItemDao {

    @Query("SELECT * FROM grocery_items ORDER BY is_purchased ASC, name ASC")
    fun observeAll(): Flow<List<GroceryItemEntity>>

    @Query("SELECT * FROM grocery_items WHERE is_purchased = 0 ORDER BY category, name")
    fun observePending(): Flow<List<GroceryItemEntity>>

    @Query("SELECT * FROM grocery_items WHERE source_recipe_id = :recipeId")
    fun observeByRecipe(recipeId: Long): Flow<List<GroceryItemEntity>>

    /** Total estimated cost of all unpurchased items that have a price set. */
    @Query(
        """
        SELECT SUM(estimated_price_per_unit * quantity)
        FROM grocery_items
        WHERE is_purchased = 0
          AND estimated_price_per_unit IS NOT NULL
        """,
    )
    fun observePendingEstimatedTotal(): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: GroceryItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<GroceryItemEntity>): List<Long>

    @Update
    suspend fun update(item: GroceryItemEntity): Int

    @Query("UPDATE grocery_items SET is_purchased = :purchased WHERE id = :id")
    suspend fun markPurchased(id: Long, purchased: Boolean): Int

    @Delete
    suspend fun delete(item: GroceryItemEntity): Int

    @Query("DELETE FROM grocery_items WHERE source_recipe_id = :recipeId")
    suspend fun deleteByRecipe(recipeId: Long): Int

    @Query("DELETE FROM grocery_items WHERE is_purchased = 1")
    suspend fun clearPurchased(): Int
}
