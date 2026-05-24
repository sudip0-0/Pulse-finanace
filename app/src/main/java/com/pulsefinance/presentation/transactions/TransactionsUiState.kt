package com.pulsefinance.presentation.transactions

import com.pulsefinance.domain.model.Category

data class TransactionsUiState(
    val isLoading: Boolean = true,
    val transactions: List<TransactionItemUiModel> = emptyList(),
    val categories: List<Category> = emptyList(),
    val searchQuery: String = "",
    val selectedCategoryId: Long? = null,
    val sortOrder: TransactionSort = TransactionSort.DateDesc,
    val deleteConfirmation: DeleteConfirmation? = null,
    val errorMessage: String? = null,
) {
    val isEmpty: Boolean
        get() = !isLoading && transactions.isEmpty()

    val hasActiveFilters: Boolean
        get() = searchQuery.isNotBlank() || selectedCategoryId != null
}

data class TransactionItemUiModel(
    val id: Long,
    val title: String,
    val merchant: String?,
    val categoryName: String,
    val categoryColorHex: String,
    val amount: String,
    val amountMinor: Long,
    val dateLabel: String,
    val expenseDateEpochDay: Long,
)

data class DeleteConfirmation(
    val expenseId: Long,
    val title: String,
)

enum class TransactionSort {
    DateDesc,
    DateAsc,
    AmountDesc,
    AmountAsc,
}
