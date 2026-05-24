package com.pulsefinance.domain.usecase

import com.pulsefinance.domain.model.CategorySpend
import com.pulsefinance.domain.model.DashboardSnapshot
import com.pulsefinance.domain.model.DomainResult
import com.pulsefinance.domain.model.Money
import com.pulsefinance.domain.repository.BudgetRepository
import com.pulsefinance.domain.repository.CategoryRepository
import com.pulsefinance.domain.repository.ExpenseRepository
import java.time.LocalDate
import java.time.YearMonth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class ObserveDashboardUseCase(
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository,
    private val budgetRepository: BudgetRepository,
    private val calculateBudgetProgress: CalculateBudgetProgressUseCase,
) {
    operator fun invoke(month: YearMonth, recentLimit: Int = 4): Flow<DashboardSnapshot> {
        val startDate = month.atDay(1)
        val endDate = month.atEndOfMonth()
        return combine(
            expenseRepository.observeExpensesBetween(startDate, endDate),
            expenseRepository.observeRecentExpenses(recentLimit),
            categoryRepository.observeCategories(),
            budgetRepository.observeBudgetForMonth(month),
        ) { expenses, recentExpenses, categories, budget ->
            val categoriesById = categories.associateBy { it.id }
            val monthlySpend = expenses.fold(Money.zero()) { total, expense -> total + expense.amount }
            val categorySpend = expenses
                .groupBy { it.categoryId }
                .mapNotNull { (categoryId, categoryExpenses) ->
                    val category = categoriesById[categoryId] ?: return@mapNotNull null
                    CategorySpend(
                        category = category,
                        amount = categoryExpenses.fold(Money.zero()) { total, expense -> total + expense.amount },
                        transactionCount = categoryExpenses.size,
                    )
                }
                .sortedByDescending { it.amount.amountMinor }
            val progress = budget?.let {
                when (val result = calculateBudgetProgress(monthlySpend, it.amount)) {
                    is DomainResult.Success -> result.value
                    is DomainResult.Failure -> null
                }
            }

            DashboardSnapshot(
                month = month,
                monthlySpend = monthlySpend,
                budget = budget,
                budgetProgress = progress,
                categorySpending = categorySpend,
                recentTransactions = recentExpenses,
                categories = categories,
            )
        }
    }
}
