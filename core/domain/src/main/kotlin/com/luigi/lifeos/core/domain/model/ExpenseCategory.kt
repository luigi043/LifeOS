package com.luigi.lifeos.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ExpenseCategory(
    val id: Long = 0,
    val name: String,
    val iconName: String = "category",
    val colorHex: String = "#6750A4",
    val isDefault: Boolean = false,
)

/**
 * System-defined categories seeded into the DB on first launch.
 * IDs are fixed so they survive reinstalls once cloud sync is added.
 */
object DefaultExpenseCategories {
    val all = listOf(
        ExpenseCategory(1,  "Food & Groceries", "shopping_cart",    "#4CAF50", true),
        ExpenseCategory(2,  "Transport",         "directions_car",   "#2196F3", true),
        ExpenseCategory(3,  "Housing",           "home",             "#FF9800", true),
        ExpenseCategory(4,  "Health",            "favorite",         "#F44336", true),
        ExpenseCategory(5,  "Entertainment",     "movie",            "#9C27B0", true),
        ExpenseCategory(6,  "Subscriptions",     "subscriptions",    "#00BCD4", true),
        ExpenseCategory(7,  "Clothing",          "checkroom",        "#E91E63", true),
        ExpenseCategory(8,  "Education",         "school",           "#3F51B5", true),
        ExpenseCategory(9,  "Personal Care",     "self_improvement", "#009688", true),
        ExpenseCategory(10, "Other",             "more_horiz",       "#607D8B", true),
    )
}
