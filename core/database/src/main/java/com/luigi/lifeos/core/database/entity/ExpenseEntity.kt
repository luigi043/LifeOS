package com.luigi.lifeos.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.luigi.lifeos.core.domain.model.Expense
import com.luigi.lifeos.core.domain.model.RecurringPeriod

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "amount_minor")          val amountMinor: Long,
    val currency: String,
    @ColumnInfo(name = "category_id")           val categoryId: Long,
    val note: String,
    @ColumnInfo(name = "is_recurring")          val isRecurring: Boolean,
    @ColumnInfo(name = "recurring_period")      val recurringPeriod: RecurringPeriod?,
    @ColumnInfo(name = "date_epoch_millis")     val dateEpochMillis: Long,
    @ColumnInfo(name = "created_at_epoch_millis") val createdAtEpochMillis: Long,
)

fun ExpenseEntity.toDomain() = Expense(
    id = id,
    amountMinor = amountMinor,
    currency = currency,
    categoryId = categoryId,
    note = note,
    isRecurring = isRecurring,
    recurringPeriod = recurringPeriod,
    dateEpochMillis = dateEpochMillis,
    createdAtEpochMillis = createdAtEpochMillis,
)

fun Expense.toEntity() = ExpenseEntity(
    id = id,
    amountMinor = amountMinor,
    currency = currency,
    categoryId = categoryId,
    note = note,
    isRecurring = isRecurring,
    recurringPeriod = recurringPeriod,
    dateEpochMillis = dateEpochMillis,
    createdAtEpochMillis = createdAtEpochMillis,
)
