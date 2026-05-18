package com.luigi.lifeos.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.luigi.lifeos.core.domain.model.GroceryItem
import com.luigi.lifeos.core.domain.model.IngredientCategory

@Entity(tableName = "grocery_items")
data class GroceryItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "quantity")
    val quantity: Double,

    @ColumnInfo(name = "unit")
    val unit: String,

    @ColumnInfo(name = "estimated_price_per_unit")
    val estimatedPricePerUnit: Double?,

    @ColumnInfo(name = "is_purchased")
    val isPurchased: Boolean = false,

    @ColumnInfo(name = "category")
    val category: String = IngredientCategory.OTHER.name,

    @ColumnInfo(name = "source_recipe_id")
    val sourceRecipeId: Long? = null,
) {
    fun toDomain() = GroceryItem(
        id = id,
        name = name,
        quantity = quantity,
        unit = unit,
        estimatedPricePerUnit = estimatedPricePerUnit,
        isPurchased = isPurchased,
        category = IngredientCategory.valueOf(category),
        sourceRecipeId = sourceRecipeId,
    )

    companion object {
        fun fromDomain(d: GroceryItem) = GroceryItemEntity(
            id = d.id,
            name = d.name,
            quantity = d.quantity,
            unit = d.unit,
            estimatedPricePerUnit = d.estimatedPricePerUnit,
            isPurchased = d.isPurchased,
            category = d.category.name,
            sourceRecipeId = d.sourceRecipeId,
        )
    }
}
