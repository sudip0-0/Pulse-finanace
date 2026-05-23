package com.pulsefinance.domain.usecase

import com.pulsefinance.domain.model.DomainError
import com.pulsefinance.domain.model.DomainResult
import com.pulsefinance.domain.model.Expense
import com.pulsefinance.domain.repository.CategoryRepository
import com.pulsefinance.domain.repository.ExpenseRepository

class UpdateExpenseUseCase(
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository,
) {
    suspend operator fun invoke(expense: Expense): DomainResult<Unit> {
        if (expense.id <= 0) {
            return DomainResult.Failure(DomainError.Validation("Expense id is required."))
        }
        validateExpense(expense)?.let { return DomainResult.Failure(it) }
        if (expenseRepository.getExpense(expense.id) == null) {
            return DomainResult.Failure(DomainError.NotFound("Expense was not found."))
        }
        if (categoryRepository.getCategory(expense.categoryId) == null) {
            return DomainResult.Failure(DomainError.Validation("Selected category does not exist."))
        }

        return try {
            expenseRepository.updateExpense(expense)
            DomainResult.Success(Unit)
        } catch (error: Throwable) {
            DomainResult.Failure(DomainError.Repository("Could not update expense.", error))
        }
    }
}
