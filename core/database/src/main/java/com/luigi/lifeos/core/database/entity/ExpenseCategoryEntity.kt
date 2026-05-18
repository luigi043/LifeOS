package com.luigi.lifeos.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.luigi.lifeos.core.domain.model.ExpenseCategory

@Entity(tableName = "expense_categories")
data class ExpenseCategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    @ColumnInfo(name = "icon_name") val iconName: String,
    @ColumnInfo(name = "color_hex") val colorHex: String,
    @ColumnInfo(name = "is_default") val isDefault: Boolean,
)

fun ExpenseCategoryEntity.toDomain() = ExpenseCategory(
    id = id,
    name = name,
    iconName = iconName,
    colorHex = colorHex,
    isDefault = isDefault,
)

fun ExpenseCategory.toEntity() = ExpenseCategoryEntity(
    id = id,
    name = name,
    iconName = iconName,
    colorHex = colorHex,
    isDefault = isDefault,
)
