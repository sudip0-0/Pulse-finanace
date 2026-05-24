package com.pulsefinance.presentation.analytics

data class AnalyticsUiState(
    val isLoading: Boolean = true,
    val periodLabel: String = "This month",
    val totalSpend: String = "",
    val categoryBreakdown: List<CategoryBreakdownItem> = emptyList(),
    val recentTransactions: List<AnalyticsTransactionItem> = emptyList(),
    val chartAccessibilitySummary: String = "",
)

enum class AnalyticsPeriod(val label: String) {
    ThisMonth("This month"),
    LastMonth("Last month"),
}

data class CategoryBreakdownItem(
    val categoryId: Long,
    val name: String,
    val colorHex: String,
    val amount: String,
    val percent: Int,
    val sweepAngle: Float,
)

data class AnalyticsTransactionItem(
    val id: Long,
    val merchant: String,
    val categoryName: String,
    val categoryColorHex: String,
    val amount: String,
    val dateLabel: String,
)
