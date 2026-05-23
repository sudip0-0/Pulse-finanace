package com.pulsefinance.domain.usecase

import com.pulsefinance.domain.model.DomainError
import com.pulsefinance.domain.model.Expense

internal fun validateExpense(expense: Expense): DomainError.Validation? {
    return when {
        expense.title.isBlank() -> DomainError.Validation("Expense title is required.")
        !expense.amount.isPositive() -> DomainError.Validation("Expense amount must be positive.")
        expense.categoryId <= 0 -> DomainError.Validation("Expense category is required.")
        expense.amount.currencyCode.isBlank() -> DomainError.Validation("Currency is required.")
        else -> null
    }
}
