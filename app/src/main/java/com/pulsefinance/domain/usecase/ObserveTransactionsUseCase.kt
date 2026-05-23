package com.pulsefinance.domain.usecase

import com.pulsefinance.domain.model.TransactionFilters
import com.pulsefinance.domain.repository.ExpenseRepository

class ObserveTransactionsUseCase(
    private val expenseRepository: ExpenseRepository,
) {
    operator fun invoke(filters: TransactionFilters = TransactionFilters()) =
        expenseRepository.observeTransactions(filters)
}
