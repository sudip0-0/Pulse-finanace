package com.pulsefinance.domain.search

import com.pulsefinance.domain.expense
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TransactionSearchMatcherTest {
    @Test
    fun matchesByTitle() {
        val expense = expense(title = "Pathao ride")
        assertTrue(TransactionSearchMatcher.matches(expense, "Pathao"))
    }

    @Test
    fun matchesByWholeMajorAmount() {
        val expense = expense(amountMinor = 28000)
        assertTrue(TransactionSearchMatcher.matches(expense, "280"))
    }

    @Test
    fun matchesByAmountWithDecimals() {
        val expense = expense(amountMinor = 241850)
        assertTrue(TransactionSearchMatcher.matches(expense, "2418.50"))
    }

    @Test
    fun matchesByFormattedAmountWithCommas() {
        val expense = expense(amountMinor = 150000)
        assertTrue(TransactionSearchMatcher.matches(expense, "1,500"))
    }

    @Test
    fun matchesByCurrencyPrefix() {
        val expense = expense(amountMinor = 28000)
        assertTrue(TransactionSearchMatcher.matches(expense, "रू 280"))
    }

    @Test
    fun doesNotMatchUnrelatedAmount() {
        val expense = expense(amountMinor = 28000)
        assertFalse(TransactionSearchMatcher.matches(expense, "450"))
    }

    @Test
    fun parseExactAmountMinorHandlesSingleDecimalDigit() {
        assertEquals(28050L, TransactionSearchMatcher.parseExactAmountMinor("280.5"))
    }
}
