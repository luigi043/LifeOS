package com.luigi.lifeos.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.luigi.lifeos.core.domain.model.Budget

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "category_id")   val categoryId: Long?,   // null = overall budget
    @ColumnInfo(name = "limit_minor")   val limitMinor: Long,
    val currency: String,
    @ColumnInfo(name = "period_start")  val periodStart: Long,   // epoch millis
    @ColumnInfo(name = "period_end")    val periodEnd: Long,     // epoch millis
)

fun BudgetEntity.toDomain() = Budget(
    id = id,
    categoryId = categoryId,
    limitMinor = limitMinor,
    currency = currency,
    periodStart = periodStart,
    periodEnd = periodEnd,
)

fun Budget.toEntity() = BudgetEntity(
    id = id,
    categoryId = categoryId,
    limitMinor = limitMinor,
    currency = currency,
    periodStart = periodStart,
    periodEnd = periodEnd,
)
