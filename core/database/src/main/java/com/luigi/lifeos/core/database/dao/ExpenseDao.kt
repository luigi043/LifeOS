package com.luigi.lifeos.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.luigi.lifeos.core.database.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    /** All expenses newest-first — used for the full expense list. */
    @Query("SELECT * FROM expenses ORDER BY date_epoch_millis DESC")
    fun observeAll(): Flow<List<ExpenseEntity>>

    /** Expenses within a calendar window — used for monthly views. */
    @Query("""
        SELECT * FROM expenses
        WHERE date_epoch_millis BETWEEN :fromMillis AND :toMillis
        ORDER BY date_epoch_millis DESC
    """)
    fun observeByPeriod(fromMillis: Long, toMillis: Long): Flow<List<ExpenseEntity>>

    /** Sum of all amount_minor in the period. Returns null if no rows exist. */
    @Query("""
        SELECT SUM(amount_minor) FROM expenses
        WHERE date_epoch_millis BETWEEN :fromMillis AND :toMillis
    """)
    suspend fun sumByPeriod(fromMillis: Long, toMillis: Long): Long?

    @Upsert
    suspend fun upsert(entity: ExpenseEntity): Long

    @Delete
    suspend fun delete(entity: ExpenseEntity): Int
}
