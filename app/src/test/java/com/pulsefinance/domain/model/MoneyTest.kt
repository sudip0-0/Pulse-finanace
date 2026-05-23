package com.pulsefinance.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class MoneyTest {
    @Test
    fun formatsNprMinorUnits() {
        assertEquals("NPR 2,418.50", Money(241850).format())
    }

    @Test
    fun addsOnlyMatchingCurrencies() {
        assertEquals(Money(300), Money(100) + Money(200))
    }
}
