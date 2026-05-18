package com.luigi.lifeos.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.luigi.lifeos.core.database.entity.BudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {

    /**
     * The overall monthly budget (category_id IS NULL) whose period overlaps
     * with the given window. Returns the most recent match if multiple exist.
     */
    @Query("""
        SELECT * FROM budgets
        WHERE category_id IS NULL
          AND period_start <= :toMillis
          AND period_end   >= :fromMillis
        ORDER BY period_start DESC
        LIMIT 1
    """)
    fun observeOverallForPeriod(fromMillis: Long, toMillis: Long): Flow<BudgetEntity?>

    /** All budgets — used by analytics to show per-category limits. */
    @Query("SELECT * FROM budgets ORDER BY period_start DESC")
    fun observeAll(): Flow<List<BudgetEntity>>

    @Upsert
    suspend fun upsert(entity: BudgetEntity): Long
}
