package com.luigi.lifeos.core.domain.model

import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Money is stored as integer minor-units (cents) to avoid floating-point error.
 * Currency is a free-form ISO 4217 code rather than an enum to support the
 * forward roadmap (multi-currency, conversion via :feature:finance converter).
 */
@JvmInline
@Serializable
value class Money(val minorUnits: Long) {
    operator fun plus(other: Money) = Money(minorUnits + other.minorUnits)
    operator fun minus(other: Money) = Money(minorUnits - other.minorUnits)
    operator fun compareTo(other: Money) = minorUnits.compareTo(other.minorUnits)

    fun toMajorUnits(): Double = minorUnits / 100.0

    companion object {
        val Zero = Money(0)
        fun fromMajor(amount: Double): Money {
            require(amount.isFinite()) { "Amount must be finite" }
            return Money(BigDecimal.valueOf(amount).movePointRight(2)
                .setScale(0, RoundingMode.HALF_UP).longValueExact())
        }

        /** Exact input parser for the supported two-decimal currencies. No grouping separators. */
        fun parseMinorUnits(text: String): Long? {
            val normalized = text.trim().replace(',', '.')
            if (!Regex("[0-9]+(?:\\.[0-9]{1,2})?").matches(normalized)) return null
            return try {
                BigDecimal(normalized).movePointRight(2).longValueExact()
            } catch (_: ArithmeticException) {
                null
            }
        }
    }
}

@Serializable
data class CurrencyAmount(val amount: Money, val currency: String)
