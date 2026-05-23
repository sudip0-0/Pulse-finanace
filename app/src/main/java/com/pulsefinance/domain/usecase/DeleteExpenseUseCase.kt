package com.pulsefinance.domain.usecase

import com.pulsefinance.domain.model.DomainError
import com.pulsefinance.domain.model.DomainResult
import com.pulsefinance.domain.repository.ExpenseRepository

class DeleteExpenseUseCase(
    private val expenseRepository: ExpenseRepository,
) {
    suspend operator fun invoke(expenseId: Long): DomainResult<Unit> {
        if (expenseId <= 0) {
            return DomainResult.Failure(DomainError.Validation("Expense id is required."))
        }
        if (expenseRepository.getExpense(expenseId) == null) {
            return DomainResult.Failure(DomainError.NotFound("Expense was not found."))
        }

        return try {
            expenseRepository.deleteExpense(expenseId)
            DomainResult.Success(Unit)
        } catch (error: Throwable) {
            DomainResult.Failure(DomainError.Repository("Could not delete expense.", error))
        }
    }
}
