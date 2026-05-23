package com.pulsefinance.data.mapper

import com.pulsefinance.data.local.entity.RecurringRuleEntity
import com.pulsefinance.domain.model.Money
import com.pulsefinance.domain.model.RecurringFrequency
import com.pulsefinance.domain.model.RecurringRule
import java.time.Instant
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import com.pulsefinance.data.local.entity.RecurringFrequency as EntityFrequency

class RecurringRuleMapperTest {

    private val fixedInstant = Instant.parse("2026-05-01T00:00:00Z")

    @Test
    fun `entity to domain maps frequency and money`() {
        val entity = RecurringRuleEntity(
            id = 1,
            title = "WorldLink internet",
            merchant = "WorldLink",
            amountMinor = 150000,
            currencyCode = "NPR",
            categoryId = 9,
            frequency = EntityFrequency.Monthly,
            interval = 1,
            startDate = LocalDate.of(2026, 1, 1),
            nextDueDate = LocalDate.of(2026, 6, 1),
            endDate = null,
            isActive = true,
            createdAt = fixedInstant,
            updatedAt = fixedInstant,
        )

        val domain = entity.toDomain()

        assertEquals(1L, domain.id)
        assertEquals("WorldLink internet", domain.title)
        assertEquals("WorldLink", domain.merchant)
        assertEquals(Money(150000, "NPR"), domain.amount)
        assertEquals(9L, domain.categoryId)
        assertEquals(RecurringFrequency.Monthly, domain.frequency)
        assertEquals(1, domain.interval)
        assertEquals(LocalDate.of(2026, 1, 1), domain.startDate)
        assertEquals(LocalDate.of(2026, 6, 1), domain.nextDueDate)
        assertNull(domain.endDate)
        assertEquals(true, domain.isActive)
    }

    @Test
    fun `domain to entity maps all frequencies`() {
        val base = RecurringRule(
            id = 2,
            title = "Rent",
            merchant = null,
            amount = Money(2500000, "NPR"),
            categoryId = 10,
            frequency = RecurringFrequency.Weekly,
            interval = 2,
            startDate = LocalDate.of(2026, 5, 1),
            nextDueDate = LocalDate.of(2026, 5, 15),
            endDate = LocalDate.of(2026, 12, 31),
            isActive = true,
            createdAt = fixedInstant,
            updatedAt = fixedInstant,
        )

        assertEquals(EntityFrequency.Weekly, base.toEntity().frequency)
        assertEquals(EntityFrequency.Monthly, base.copy(frequency = RecurringFrequency.Monthly).toEntity().frequency)
        assertEquals(EntityFrequency.Yearly, base.copy(frequency = RecurringFrequency.Yearly).toEntity().frequency)
    }

    @Test
    fun `round trip preserves all data`() {
        val original = RecurringRuleEntity(
            id = 5,
            title = "NTC recharge",
            merchant = "NTC",
            amountMinor = 50000,
            currencyCode = "NPR",
            categoryId = 6,
            frequency = EntityFrequency.Yearly,
            interval = 1,
            startDate = LocalDate.of(2026, 1, 15),
            nextDueDate = LocalDate.of(2027, 1, 15),
            endDate = null,
            isActive = true,
            createdAt = fixedInstant,
            updatedAt = fixedInstant,
        )

        assertEquals(original, original.toDomain().toEntity())
    }
}
