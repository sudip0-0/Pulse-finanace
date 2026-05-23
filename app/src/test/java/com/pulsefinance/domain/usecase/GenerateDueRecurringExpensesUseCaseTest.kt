package com.pulsefinance.domain.usecase

import com.pulsefinance.domain.FakeExpenseRepository
import com.pulsefinance.domain.FakeRecurringRuleRepository
import com.pulsefinance.domain.expense
import com.pulsefinance.domain.model.DomainResult
import com.pulsefinance.domain.model.RecurringFrequency
import com.pulsefinance.domain.recurringRule
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GenerateDueRecurringExpensesUseCaseTest {
    private val clock = Clock.fixed(Instant.parse("2026-05-23T10:15:30Z"), ZoneOffset.UTC)

    @Test
    fun generatesEachMissedDueDateAndAdvancesRule() = runBlocking {
        val expenseRepository = FakeExpenseRepository()
        val recurringRepository = FakeRecurringRuleRepository(listOf(recurringRule(nextDueDate = LocalDate.of(2026, 3, 1))))
        val useCase = GenerateDueRecurringExpensesUseCase(recurringRepository, expenseRepository, clock)

        val result = useCase(LocalDate.of(2026, 5, 23))

        val ids = (result as DomainResult.Success).value
        assertEquals(3, ids.size)
    }

    @Test
    fun skipsAlreadyGeneratedDueDate() = runBlocking {
        val existing = expense(id = 99, date = LocalDate.of(2026, 5, 1), recurringRuleId = 1)
        val expenseRepository = FakeExpenseRepository(listOf(existing))
        val recurringRepository = FakeRecurringRuleRepository(listOf(recurringRule(nextDueDate = LocalDate.of(2026, 5, 1))))
        val useCase = GenerateDueRecurringExpensesUseCase(recurringRepository, expenseRepository, clock)

        val result = useCase(LocalDate.of(2026, 5, 23))

        val ids = (result as DomainResult.Success).value
        assertEquals(emptyList<Long>(), ids)
    }

    @Test
    fun preservesMonthEndRecurringSchedule() = runBlocking {
        val rule = recurringRule(
            nextDueDate = LocalDate.of(2026, 1, 31),
            startDate = LocalDate.of(2026, 1, 31),
        )

        assertEquals(LocalDate.of(2026, 2, 28), rule.nextDateAfter(LocalDate.of(2026, 1, 31)))
        assertEquals(LocalDate.of(2026, 3, 31), rule.nextDateAfter(LocalDate.of(2026, 2, 28)))
    }

    @Test
    fun inactiveRulesAreNotGeneratedByRepositoryContract() = runBlocking {
        val expenseRepository = FakeExpenseRepository()
        val recurringRepository = FakeRecurringRuleRepository(listOf(recurringRule().copy(isActive = false)))
        val useCase = GenerateDueRecurringExpensesUseCase(recurringRepository, expenseRepository, clock)

        val result = useCase(LocalDate.of(2026, 5, 23))

        val ids = (result as DomainResult.Success).value
        assertEquals(emptyList<Long>(), ids)
    }

    @Test
    fun endDateBoundaryGeneratesDueDateOnEndDate() = runBlocking {
        val expenseRepository = FakeExpenseRepository()
        val recurringRepository = FakeRecurringRuleRepository(
            listOf(
                recurringRule(nextDueDate = LocalDate.of(2026, 5, 1))
                    .copy(endDate = LocalDate.of(2026, 5, 1)),
            ),
        )
        val useCase = GenerateDueRecurringExpensesUseCase(recurringRepository, expenseRepository, clock)

        val result = useCase(LocalDate.of(2026, 5, 23))

        val ids = (result as DomainResult.Success).value
        assertEquals(1, ids.size)
    }

    @Test
    fun weeklyRuleGeneratesCorrectDueDates() = runBlocking {
        val expenseRepository = FakeExpenseRepository()
        val rule = recurringRule(
            nextDueDate = LocalDate.of(2026, 5, 5),
            startDate = LocalDate.of(2026, 5, 5),
        ).copy(frequency = RecurringFrequency.Weekly)
        val recurringRepository = FakeRecurringRuleRepository(listOf(rule))
        val useCase = GenerateDueRecurringExpensesUseCase(recurringRepository, expenseRepository, clock)

        val result = useCase(LocalDate.of(2026, 5, 23))

        val ids = (result as DomainResult.Success).value
        assertEquals(3, ids.size) // May 5, 12, 19 (May 26 is after today)
    }

    @Test
    fun yearlyRuleGeneratesOncePerYear() = runBlocking {
        val expenseRepository = FakeExpenseRepository()
        val rule = recurringRule(
            nextDueDate = LocalDate.of(2026, 1, 15),
            startDate = LocalDate.of(2026, 1, 15),
        ).copy(frequency = RecurringFrequency.Yearly)
        val recurringRepository = FakeRecurringRuleRepository(listOf(rule))
        val useCase = GenerateDueRecurringExpensesUseCase(recurringRepository, expenseRepository, clock)

        val result = useCase(LocalDate.of(2026, 5, 23))

        val ids = (result as DomainResult.Success).value
        assertEquals(1, ids.size) // Only Jan 15, 2026; next is Jan 15, 2027
    }

    @Test
    fun monthlyDay30DoesNotDriftAfterFebruary() = runBlocking {
        // Rule starts on Jan 30 — should stay on 30 in months that have 30+ days
        val rule = recurringRule(
            nextDueDate = LocalDate.of(2026, 1, 30),
            startDate = LocalDate.of(2026, 1, 30),
        )

        val feb = rule.nextDateAfter(LocalDate.of(2026, 1, 30))
        assertEquals(LocalDate.of(2026, 2, 28), feb) // Feb has 28 days, clamped

        val mar = rule.nextDateAfter(feb)
        assertEquals(LocalDate.of(2026, 3, 30), mar) // Back to 30, not stuck at 28
    }

    @Test
    fun concurrentInvocationReturnsEmptyWithoutDuplicates() = runBlocking {
        val expenseRepository = FakeExpenseRepository()
        val recurringRepository = FakeRecurringRuleRepository(listOf(recurringRule(nextDueDate = LocalDate.of(2026, 5, 1))))
        val useCase = GenerateDueRecurringExpensesUseCase(recurringRepository, expenseRepository, clock)

        // First call should succeed
        val result1 = useCase(LocalDate.of(2026, 5, 23))
        assertTrue((result1 as DomainResult.Success).value.isNotEmpty())

        // Second call with same state — rule already advanced, no new expenses
        val result2 = useCase(LocalDate.of(2026, 5, 23))
        assertTrue((result2 as DomainResult.Success).value.isEmpty())
    }
}
