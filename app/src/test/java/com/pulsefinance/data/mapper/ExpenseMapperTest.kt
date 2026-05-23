package com.pulsefinance.data.mapper

import com.pulsefinance.data.local.entity.ExpenseEntity
import com.pulsefinance.domain.model.Expense
import com.pulsefinance.domain.model.Money
import com.pulsefinance.domain.model.PaymentMethod
import java.time.Instant
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ExpenseMapperTest {

    private val fixedInstant = Instant.parse("2026-05-23T10:15:30Z")

    @Test
    fun `entity to domain maps all fields correctly`() {
        val entity = ExpenseEntity(
            id = 1,
            title = "Pathao ride",
            merchant = "Pathao",
            amountMinor = 28000,
            currencyCode = "NPR",
            categoryId = 3,
            paymentMethod = "Esewa",
            expenseDate = LocalDate.of(2026, 5, 23),
            note = "Office commute",
            isRecurringGenerated = false,
            recurringRuleId = null,
            createdAt = fixedInstant,
            updatedAt = fixedInstant,
        )

        val domain = entity.toDomain()

        assertEquals(1L, domain.id)
        assertEquals("Pathao ride", domain.title)
        assertEquals("Pathao", domain.merchant)
        assertEquals(Money(28000, "NPR"), domain.amount)
        assertEquals(3L, domain.categoryId)
        assertEquals(PaymentMethod.Esewa, domain.paymentMethod)
        assertEquals(LocalDate.of(2026, 5, 23), domain.expenseDate)
        assertEquals("Office commute", domain.note)
        assertEquals(false, domain.isRecurringGenerated)
        assertNull(domain.recurringRuleId)
        assertEquals(fixedInstant, domain.createdAt)
        assertEquals(fixedInstant, domain.updatedAt)
    }

    @Test
    fun `domain to entity maps all fields correctly`() {
        val domain = Expense(
            id = 2,
            title = "Foodmandu order",
            merchant = "Foodmandu",
            amount = Money(85000, "NPR"),
            categoryId = 1,
            paymentMethod = PaymentMethod.Khalti,
            expenseDate = LocalDate.of(2026, 5, 20),
            note = null,
            isRecurringGenerated = true,
            recurringRuleId = 5,
            createdAt = fixedInstant,
            updatedAt = fixedInstant,
        )

        val entity = domain.toEntity()

        assertEquals(2L, entity.id)
        assertEquals("Foodmandu order", entity.title)
        assertEquals("Foodmandu", entity.merchant)
        assertEquals(85000L, entity.amountMinor)
        assertEquals("NPR", entity.currencyCode)
        assertEquals(1L, entity.categoryId)
        assertEquals("Khalti", entity.paymentMethod)
        assertEquals(LocalDate.of(2026, 5, 20), entity.expenseDate)
        assertNull(entity.note)
        assertEquals(true, entity.isRecurringGenerated)
        assertEquals(5L, entity.recurringRuleId)
        assertEquals(fixedInstant, entity.createdAt)
        assertEquals(fixedInstant, entity.updatedAt)
    }

    @Test
    fun `null payment method maps correctly in both directions`() {
        val entity = ExpenseEntity(
            id = 3,
            title = "Bus fare",
            merchant = null,
            amountMinor = 3000,
            currencyCode = "NPR",
            categoryId = 3,
            paymentMethod = null,
            expenseDate = LocalDate.of(2026, 5, 22),
            note = null,
            isRecurringGenerated = false,
            recurringRuleId = null,
            createdAt = fixedInstant,
            updatedAt = fixedInstant,
        )

        val domain = entity.toDomain()
        assertNull(domain.paymentMethod)

        val backToEntity = domain.toEntity()
        assertNull(backToEntity.paymentMethod)
    }

    @Test
    fun `round trip preserves all data`() {
        val original = ExpenseEntity(
            id = 10,
            title = "Daraz order",
            merchant = "Daraz",
            amountMinor = 250000,
            currencyCode = "NPR",
            categoryId = 4,
            paymentMethod = "Cash",
            expenseDate = LocalDate.of(2026, 5, 15),
            note = "Birthday gift",
            isRecurringGenerated = false,
            recurringRuleId = null,
            createdAt = fixedInstant,
            updatedAt = fixedInstant,
        )

        val roundTripped = original.toDomain().toEntity()

        assertEquals(original, roundTripped)
    }
}
