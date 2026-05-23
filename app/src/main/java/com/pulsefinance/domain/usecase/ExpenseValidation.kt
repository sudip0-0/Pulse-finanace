package com.pulsefinance.domain.usecase

import com.pulsefinance.domain.model.DomainError
import com.pulsefinance.domain.model.Expense
import java.time.Clock
import java.time.LocalDate

internal fun validateExpense(expense: Expense, clock: Clock): DomainError.Validation? {
    return when {
        expense.title.isBlank() -> DomainError.Validation("Expense title is required.")
        !expense.amount.isPositive() -> DomainError.Validation("Expense amount must be positive.")
        expense.categoryId <= 0 -> DomainError.Validation("Expense category is required.")
        expense.amount.currencyCode.isBlank() -> DomainError.Validation("Currency is required.")
        expense.expenseDate.isAfter(LocalDate.now(clock)) -> DomainError.Validation("Expense date cannot be in the future.")
        expense.updatedAt.isBefore(expense.createdAt) -> DomainError.Validation("Updated time cannot be before created time.")
        else -> null
    }
}
