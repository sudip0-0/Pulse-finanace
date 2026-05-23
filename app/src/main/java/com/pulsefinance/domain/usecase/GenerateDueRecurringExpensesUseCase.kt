package com.pulsefinance.domain.usecase

import com.pulsefinance.domain.model.DomainError
import com.pulsefinance.domain.model.DomainResult
import com.pulsefinance.domain.model.Expense
import com.pulsefinance.domain.model.RecurringRule
import com.pulsefinance.domain.repository.ExpenseRepository
import com.pulsefinance.domain.repository.RecurringRuleRepository
import java.time.Clock
import java.time.Instant
import java.time.LocalDate

class GenerateDueRecurringExpensesUseCase(
    private val recurringRuleRepository: RecurringRuleRepository,
    private val expenseRepository: ExpenseRepository,
    private val clock: Clock = Clock.systemDefaultZone(),
) {
    suspend operator fun invoke(today: LocalDate = LocalDate.now(clock)): DomainResult<List<Long>> {
        return try {
            val generatedIds = mutableListOf<Long>()
            val dueRules = recurringRuleRepository.getActiveRulesDueOnOrBefore(today)
            dueRules.forEach { rule ->
                generateForRule(rule, today).forEach { expense ->
                    generatedIds += expenseRepository.addExpense(expense)
                }
            }
            DomainResult.Success(generatedIds)
        } catch (error: Throwable) {
            DomainResult.Failure(DomainError.Repository("Could not generate due recurring expenses.", error))
        }
    }

    private suspend fun generateForRule(rule: RecurringRule, today: LocalDate): List<Expense> {
        val now = Instant.now(clock)
        val expenses = mutableListOf<Expense>()
        var dueDate = rule.nextDueDate
        var nextDueDate = dueDate

        while (!dueDate.isAfter(today) && (rule.endDate == null || !dueDate.isAfter(rule.endDate))) {
            val alreadyGenerated = rule.id > 0 && expenseRepository.hasGeneratedExpenseForRecurringRule(rule.id, dueDate)
            if (!alreadyGenerated) {
                expenses += Expense(
                    title = rule.title,
                    merchant = rule.merchant,
                    amount = rule.amount,
                    categoryId = rule.categoryId,
                    paymentMethod = null,
                    expenseDate = dueDate,
                    note = null,
                    isRecurringGenerated = true,
                    recurringRuleId = rule.id.takeIf { it > 0 },
                    createdAt = now,
                    updatedAt = now,
                )
            }
            nextDueDate = rule.nextDateAfter(dueDate)
            dueDate = nextDueDate
        }

        recurringRuleRepository.updateRule(
            rule.copy(
                nextDueDate = nextDueDate,
                isActive = rule.endDate == null || !nextDueDate.isAfter(rule.endDate),
                updatedAt = now,
            ),
        )
        return expenses
    }
}
