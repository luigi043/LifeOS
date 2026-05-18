package com.luigi.lifeos.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.luigi.lifeos.core.domain.model.Income
import com.luigi.lifeos.core.domain.model.RecurringPeriod

@Entity(tableName = "income")
data class IncomeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "amount_minor")            val amountMinor: Long,
    val currency: String,
    val source: String,
    @ColumnInfo(name = "is_recurring")            val isRecurring: Boolean,
    @ColumnInfo(name = "recurring_period")        val recurringPeriod: RecurringPeriod?,
    @ColumnInfo(name = "date_epoch_millis")       val dateEpochMillis: Long,
    @ColumnInfo(name = "created_at_epoch_millis") val createdAtEpochMillis: Long,
)

fun IncomeEntity.toDomain() = Income(
    id = id,
    amountMinor = amountMinor,
    currency = currency,
    source = source,
    isRecurring = isRecurring,
    recurringPeriod = recurringPeriod,
    dateEpochMillis = dateEpochMillis,
    createdAtEpochMillis = createdAtEpochMillis,
)

fun Income.toEntity() = IncomeEntity(
    id = id,
    amountMinor = amountMinor,
    currency = currency,
    source = source,
    isRecurring = isRecurring,
    recurringPeriod = recurringPeriod,
    dateEpochMillis = dateEpochMillis,
    createdAtEpochMillis = createdAtEpochMillis,
)
