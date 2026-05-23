package com.pulsefinance.presentation.dashboard

import com.pulsefinance.domain.model.BudgetProgress
import com.pulsefinance.domain.model.BudgetStatus
import com.pulsefinance.domain.model.Money

data class DashboardUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val monthLabel: String = "",
    val monthlySpend: String = Money.zero().format(),
    val budgetState: BudgetUiState? = null,
    val categorySpending: List<CategorySpendUiModel> = emptyList(),
    val recentTransactions: List<TransactionUiModel> = emptyList(),
    val quickAddItems: List<String> = emptyList(),
) {
    val isEmpty: Boolean
        get() = !isLoading && errorMessage == null &&
            categorySpending.isEmpty() && recentTransactions.isEmpty()
}

data class BudgetUiState(
    val remainingLabel: String,
    val progressLabel: String,
    val percent: Float,
    val percentLabel: String,
    val status: BudgetStatus,
)

data class CategorySpendUiModel(
    val categoryId: Long,
    val name: String,
    val amount: String,
    val percent: String,
    val colorHex: String,
)

data class TransactionUiModel(
    val id: Long,
    val merchant: String,
    val category: String,
    val amount: String,
    val dateLabel: String,
    val colorHex: String,
)

fun BudgetProgress.toUiState(): BudgetUiState = BudgetUiState(
    remainingLabel = "${remaining.format()} left",
    progressLabel = "${spent.format()} of ${budget.format()}",
    percent = percent.coerceIn(0f, 1f),
    percentLabel = "${(percent * 100).toInt()}%",
    status = status,
)
