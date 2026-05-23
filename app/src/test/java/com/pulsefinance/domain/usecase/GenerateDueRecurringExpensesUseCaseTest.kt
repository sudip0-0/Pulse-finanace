package com.pulsefinance.domain.usecase

import com.pulsefinance.domain.FakeExpenseRepository
import com.pulsefinance.domain.FakeRecurringRuleRepository
import com.pulsefinance.domain.expense
import com.pulsefinance.domain.model.DomainResult
import com.pulsefinance.domain.recurringRule
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
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
}
