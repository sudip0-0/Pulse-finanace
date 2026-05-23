package com.pulsefinance.domain.model

import java.time.YearMonth

data class CategorySpend(
    val category: Category,
    val amount: Money,
    val transactionCount: Int,
) {
    init {
        require(transactionCount >= 0) { "Transaction count cannot be negative." }
    }
}

data class BudgetProgress(
    val spent: Money,
    val budget: Money,
    val remaining: Money,
    val percent: Float,
    val status: BudgetStatus,
)

enum class BudgetStatus {
    Under,
    Warning,
    Danger,
    OverBudget,
}

data class DashboardSnapshot(
    val month: YearMonth,
    val monthlySpend: Money,
    val budget: Budget?,
    val budgetProgress: BudgetProgress?,
    val categorySpending: List<CategorySpend>,
    val recentTransactions: List<Expense>,
)

data class TransactionFilters(
    val startDate: java.time.LocalDate? = null,
    val endDate: java.time.LocalDate? = null,
    val categoryId: Long? = null,
    val searchQuery: String? = null,
)
