package com.luigi.lifeos.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.luigi.lifeos.core.database.entity.ExpenseCategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseCategoryDao {

    @Query("SELECT * FROM expense_categories ORDER BY name ASC")
    fun observeAll(): Flow<List<ExpenseCategoryEntity>>

    @Query("SELECT COUNT(*) FROM expense_categories")
    suspend fun count(): Int

    @Upsert
    suspend fun upsert(entity: ExpenseCategoryEntity): Long

    @Upsert
    suspend fun upsertAll(entities: List<ExpenseCategoryEntity>): List<Long>
}
