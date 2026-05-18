package com.luigi.lifeos.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.luigi.lifeos.core.domain.model.Ingredient
import com.luigi.lifeos.core.domain.model.IngredientCategory

@Entity(tableName = "ingredients")
data class IngredientEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "unit")
    val unit: String,

    @ColumnInfo(name = "estimated_price_per_unit")
    val estimatedPricePerUnit: Double?,

    @ColumnInfo(name = "category")
    val category: String = IngredientCategory.OTHER.name,
) {
    fun toDomain() = Ingredient(
        id = id,
        name = name,
        unit = unit,
        estimatedPricePerUnit = estimatedPricePerUnit,
        category = IngredientCategory.valueOf(category),
    )

    companion object {
        fun fromDomain(d: Ingredient) = IngredientEntity(
            id = d.id,
            name = d.name,
            unit = d.unit,
            estimatedPricePerUnit = d.estimatedPricePerUnit,
            category = d.category.name,
        )
    }
}
