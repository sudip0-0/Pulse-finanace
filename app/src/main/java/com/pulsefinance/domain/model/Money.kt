package com.pulsefinance.domain.model

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
        require(amountMinor != Long.MIN_VALUE) { "Money amount is too small to format safely." }
        val absoluteMinor = if (amountMinor < 0) -amountMinor else amountMinor
        val major = absoluteMinor / MINOR_UNITS_PER_MAJOR
        val minor = absoluteMinor % MINOR_UNITS_PER_MAJOR
        val formatted = "${major.withGrouping()}." + minor.toString().padStart(2, '0')
        val sign = if (amountMinor < 0) "-" else ""
        val symbol = displaySymbol(currencyCode)
        return "$sign$symbol $formatted"
    }

    private fun requireSameCurrency(other: Money) {
        require(currencyCode == other.currencyCode) { "Currency mismatch: $currencyCode and ${other.currencyCode}." }
    }

    companion object {
        const val DEFAULT_CURRENCY = "NPR"
        const val MINOR_UNITS_PER_MAJOR = 100L

        fun zero(currencyCode: String = DEFAULT_CURRENCY): Money = Money(0, currencyCode)

        private fun displaySymbol(currencyCode: String): String = when (currencyCode) {
            "NPR" -> "रू"
            else -> currencyCode
        }
    }
}

private fun Long.withGrouping(): String {
    val digits = toString()
    return digits
        .reversed()
        .chunked(3)
        .joinToString(",")
        .reversed()
}
