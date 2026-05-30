package com.pulsefinance.domain.search

import com.pulsefinance.domain.model.Expense
import com.pulsefinance.domain.model.Money

object TransactionSearchMatcher {
    fun matches(expense: Expense, query: String): Boolean {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return true

        if (matchesText(expense, trimmed)) return true
        return matchesAmount(expense.amount.amountMinor, trimmed)
    }

    fun parseExactAmountMinor(query: String): Long? = parseAmountMinor(cleanAmountQuery(query))

    fun amountDigitsPattern(query: String): String? {
        val digits = query.filter(Char::isDigit)
        return digits.takeIf { it.isNotEmpty() }
    }

    private fun matchesText(expense: Expense, query: String): Boolean {
        return listOf(expense.title, expense.merchant.orEmpty(), expense.note.orEmpty())
            .any { it.contains(query, ignoreCase = true) }
    }

    private fun matchesAmount(amountMinor: Long, query: String): Boolean {
        parseExactAmountMinor(query)?.let { parsed ->
            if (amountMinor == parsed) return true
        }

        amountDigitsPattern(query)?.let { digits ->
            if (amountMinor.toString().contains(digits)) return true
        }

        val cleaned = cleanAmountQuery(query)
        if (cleaned.isNotEmpty() && formatAmountForSearch(amountMinor).contains(cleaned, ignoreCase = true)) {
            return true
        }

        return false
    }

    private fun cleanAmountQuery(query: String): String {
        return query
            .replace(Regex("""[रूRsNPR,\s]""", RegexOption.IGNORE_CASE), "")
            .trim()
    }

    private fun parseAmountMinor(text: String): Long? {
        if (text.isBlank()) return null
        val parts = text.split(".")
        val major = parts[0].toLongOrNull() ?: return null
        val minor = when {
            parts.size == 1 -> 0L
            parts[1].isEmpty() -> 0L
            parts[1].length == 1 -> parts[1].toLongOrNull()?.times(10) ?: return null
            else -> parts[1].take(2).toLongOrNull() ?: return null
        }
        return major * Money.MINOR_UNITS_PER_MAJOR + minor
    }

    private fun formatAmountForSearch(amountMinor: Long): String {
        val absoluteMinor = if (amountMinor < 0) -amountMinor else amountMinor
        val major = absoluteMinor / Money.MINOR_UNITS_PER_MAJOR
        val minor = absoluteMinor % Money.MINOR_UNITS_PER_MAJOR
        return "${major.withGrouping()}." + minor.toString().padStart(2, '0')
    }

    private fun Long.withGrouping(): String {
        val digits = toString()
        return digits
            .reversed()
            .chunked(3)
            .joinToString(",")
            .reversed()
    }
}
