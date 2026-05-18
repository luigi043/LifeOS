package com.luigi.lifeos.core.domain.model

import kotlinx.serialization.Serializable

/**
 * A spending ceiling for a given calendar period.
 *
 * [categoryId] = null means this is the overall monthly budget (used for the
 * top-level "Budget remaining" card). Per-category budgets set [categoryId].
 *
 * [periodStart] / [periodEnd] are epoch-millis representing the first and last
 * millisecond of a calendar month (or any custom window). The repo queries
 * WHERE date BETWEEN periodStart AND periodEnd.
 */
@Serializable
data class Budget(
    val id: Long = 0,
    val categoryId: Long? = null,
    val limitMinor: Long,
    val currency: String = "EUR",
    val periodStart: Long,
    val periodEnd: Long,
)

fun Budget.limitMoney(): Money = Money(limitMinor)
