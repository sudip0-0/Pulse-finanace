package com.pulsefinance.domain.model

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.abs

data class Money(
    val amountMinor: Long,
    val currencyCode: String = DEFAULT_CURRENCY,
) : Comparable<Money> {
    init {
        require(currencyCode.isNotBlank()) { "Currency code is required." }
    }

    operator fun plus(other: Money): Money {
        requireSameCurrency(other)
        return copy(amountMinor = amountMinor + other.amountMinor)
    }

    operator fun minus(other: Money): Money {
        requireSameCurrency(other)
        return copy(amountMinor = amountMinor - other.amountMinor)
    }

    override fun compareTo(other: Money): Int {
        requireSameCurrency(other)
        return amountMinor.compareTo(other.amountMinor)
    }

    fun isPositive(): Boolean = amountMinor > 0

    fun format(): String {
        val symbols = DecimalFormatSymbols(Locale.US)
        val formatter = DecimalFormat("#,##0.00", symbols)
        val major = abs(amountMinor) / MINOR_UNITS_PER_MAJOR
        val minor = abs(amountMinor) % MINOR_UNITS_PER_MAJOR
        val formatted = formatter.format(major + minor.toDouble() / MINOR_UNITS_PER_MAJOR)
        val sign = if (amountMinor < 0) "-" else ""
        return "$sign$currencyCode $formatted"
    }

    private fun requireSameCurrency(other: Money) {
        require(currencyCode == other.currencyCode) { "Currency mismatch: $currencyCode and ${other.currencyCode}." }
    }

    companion object {
        const val DEFAULT_CURRENCY = "NPR"
        const val MINOR_UNITS_PER_MAJOR = 100L

        fun zero(currencyCode: String = DEFAULT_CURRENCY): Money = Money(0, currencyCode)
    }
}
