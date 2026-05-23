package com.pulsefinance.domain.usecase

import com.pulsefinance.domain.model.DomainError
import com.pulsefinance.domain.model.DomainResult
import com.pulsefinance.domain.model.Expense
import com.pulsefinance.domain.repository.CategoryRepository
import com.pulsefinance.domain.repository.ExpenseRepository

class AddExpenseUseCase(
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository,
) {
    suspend operator fun invoke(expense: Expense): DomainResult<Long> {
        validateExpense(expense)?.let { return DomainResult.Failure(it) }
        if (categoryRepository.getCategory(expense.categoryId) == null) {
            return DomainResult.Failure(DomainError.Validation("Selected category does not exist."))
        }

        return try {
            DomainResult.Success(expenseRepository.addExpense(expense))
        } catch (error: Throwable) {
            DomainResult.Failure(DomainError.Repository("Could not add expense.", error))
        }
    }
}
