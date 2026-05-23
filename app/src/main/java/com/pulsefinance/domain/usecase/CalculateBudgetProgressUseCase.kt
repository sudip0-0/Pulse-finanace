package com.pulsefinance.domain.usecase

import com.pulsefinance.domain.model.BudgetProgress
import com.pulsefinance.domain.model.BudgetStatus
import com.pulsefinance.domain.model.DomainError
import com.pulsefinance.domain.model.DomainResult
import com.pulsefinance.domain.model.Money

class CalculateBudgetProgressUseCase {
    operator fun invoke(spent: Money, budget: Money): DomainResult<BudgetProgress> {
        if (!budget.isPositive()) {
            return DomainResult.Failure(DomainError.Validation("Budget amount must be positive."))
        }
        if (spent.currencyCode != budget.currencyCode) {
            return DomainResult.Failure(DomainError.Validation("Budget and spend currencies must match."))
        }

        val percent = spent.amountMinor.toFloat() / budget.amountMinor.toFloat()
        val status = when {
            percent > 1f -> BudgetStatus.OverBudget
            percent > 0.9f -> BudgetStatus.Danger
            percent >= 0.7f -> BudgetStatus.Warning
            else -> BudgetStatus.Under
        }

        return DomainResult.Success(
            BudgetProgress(
                spent = spent,
                budget = budget,
                remaining = Money(budget.amountMinor - spent.amountMinor, budget.currencyCode),
                percent = percent,
                status = status,
            ),
        )
    }
}
