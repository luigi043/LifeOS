package com.luigi.lifeos.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.luigi.lifeos.core.database.entity.IncomeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IncomeDao {

    @Query("SELECT * FROM income ORDER BY date_epoch_millis DESC")
    fun observeAll(): Flow<List<IncomeEntity>>

    @Query("""
        SELECT * FROM income
        WHERE date_epoch_millis BETWEEN :fromMillis AND :toMillis
        ORDER BY date_epoch_millis DESC
    """)
    fun observeByPeriod(fromMillis: Long, toMillis: Long): Flow<List<IncomeEntity>>

    @Upsert
    suspend fun upsert(entity: IncomeEntity): Long

    @Delete
    suspend fun delete(entity: IncomeEntity): Int
}
