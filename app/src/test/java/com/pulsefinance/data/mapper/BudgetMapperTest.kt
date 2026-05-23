package com.pulsefinance.data.mapper

import com.pulsefinance.data.local.entity.BudgetEntity
import com.pulsefinance.domain.model.Budget
import com.pulsefinance.domain.model.Money
import java.time.Instant
import java.time.YearMonth
import org.junit.Assert.assertEquals
import org.junit.Test

class BudgetMapperTest {

    private val fixedInstant = Instant.parse("2026-05-01T00:00:00Z")

    @Test
    fun `entity to domain maps money correctly`() {
        val entity = BudgetEntity(
            id = 1,
            month = YearMonth.of(2026, 5),
            amountMinor = 5000000,
            currencyCode = "NPR",
            createdAt = fixedInstant,
            updatedAt = fixedInstant,
        )

        val domain = entity.toDomain()

        assertEquals(1L, domain.id)
        assertEquals(YearMonth.of(2026, 5), domain.month)
        assertEquals(Money(5000000, "NPR"), domain.amount)
        assertEquals(fixedInstant, domain.createdAt)
        assertEquals(fixedInstant, domain.updatedAt)
    }

    @Test
    fun `domain to entity decomposes money`() {
        val domain = Budget(
            id = 2,
            month = YearMonth.of(2026, 6),
            amount = Money(3000000, "NPR"),
            createdAt = fixedInstant,
            updatedAt = fixedInstant,
        )

        val entity = domain.toEntity()

        assertEquals(2L, entity.id)
        assertEquals(YearMonth.of(2026, 6), entity.month)
        assertEquals(3000000L, entity.amountMinor)
        assertEquals("NPR", entity.currencyCode)
    }

    @Test
    fun `round trip preserves all data`() {
        val original = BudgetEntity(
            id = 3,
            month = YearMonth.of(2026, 7),
            amountMinor = 4000000,
            currencyCode = "NPR",
            createdAt = fixedInstant,
            updatedAt = fixedInstant,
        )

        assertEquals(original, original.toDomain().toEntity())
    }
}
