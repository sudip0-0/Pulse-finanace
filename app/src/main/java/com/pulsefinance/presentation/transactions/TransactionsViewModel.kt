package com.pulsefinance.presentation.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulsefinance.domain.model.Category
import com.pulsefinance.domain.model.DomainResult
import com.pulsefinance.domain.model.Expense
import com.pulsefinance.domain.model.TransactionFilters
import com.pulsefinance.domain.repository.CategoryRepository
import com.pulsefinance.domain.usecase.DeleteExpenseUseCase
import com.pulsefinance.domain.usecase.ObserveTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val observeTransactions: ObserveTransactionsUseCase,
    private val deleteExpenseUseCase: DeleteExpenseUseCase,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionsUiState())
    val uiState: StateFlow<TransactionsUiState> = _uiState

    private var observeJob: Job? = null

    init {
        loadCategories()
        observeWithCurrentFilters()
    }

    fun onSearchChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query, errorMessage = null)
        debouncedObserve()
    }

    fun onCategoryFilterSelected(categoryId: Long?) {
        val current = _uiState.value.selectedCategoryId
        val newId = if (current == categoryId) null else categoryId
        _uiState.value = _uiState.value.copy(selectedCategoryId = newId, errorMessage = null)
        observeWithCurrentFilters()
    }

    fun onSortChanged(sort: TransactionSort) {
        _uiState.value = _uiState.value.copy(sortOrder = sort)
        // Re-sort existing transactions immediately without re-querying
        val current = _uiState.value
        if (current.transactions.isNotEmpty()) {
            val resorted = when (sort) {
                TransactionSort.DateDesc -> current.transactions.sortedByDescending { it.expenseDateEpochDay }
                TransactionSort.DateAsc -> current.transactions.sortedBy { it.expenseDateEpochDay }
                TransactionSort.AmountDesc -> current.transactions.sortedByDescending { it.amountMinor }
                TransactionSort.AmountAsc -> current.transactions.sortedBy { it.amountMinor }
            }
            _uiState.value = _uiState.value.copy(transactions = resorted)
        }
    }

    fun onDeleteRequested(transaction: TransactionItemUiModel) {
        _uiState.value = _uiState.value.copy(
            deleteConfirmation = DeleteConfirmation(transaction.id, transaction.title),
        )
    }

    fun onDeleteConfirmed() {
        val confirmation = _uiState.value.deleteConfirmation ?: return
        _uiState.value = _uiState.value.copy(deleteConfirmation = null)
        viewModelScope.launch {
            when (val result = deleteExpenseUseCase(confirmation.expenseId)) {
                is DomainResult.Success -> { /* list updates reactively */ }
                is DomainResult.Failure -> {
                    _uiState.value = _uiState.value.copy(errorMessage = result.error.message)
                }
            }
        }
    }

    fun onDeleteDismissed() {
        _uiState.value = _uiState.value.copy(deleteConfirmation = null)
    }

    fun onClearFilters() {
        _uiState.value = _uiState.value.copy(searchQuery = "", selectedCategoryId = null)
        observeWithCurrentFilters()
    }

    private var searchDebounceJob: Job? = null

    private fun debouncedObserve() {
        searchDebounceJob?.cancel()
        searchDebounceJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MS)
            observeWithCurrentFilters()
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            val categories = categoryRepository.observeCategories().first()
            _uiState.value = _uiState.value.copy(categories = categories)
        }
    }

    private fun observeWithCurrentFilters() {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            val state = _uiState.value
            val filters = TransactionFilters(
                searchQuery = state.searchQuery.ifBlank { null },
                categoryId = state.selectedCategoryId,
            )
            observeTransactions(filters)
                .catch { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Could not load transactions.",
                    )
                }
                .combine(categoryRepository.observeCategories()) { expenses, categories ->
                    expenses to categories
                }
                .collect { (expenses, categories) ->
                    val categoriesById = categories.associateBy { it.id }
                    val sorted = sortExpenses(expenses, _uiState.value.sortOrder)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        transactions = sorted.map { it.toUiModel(categoriesById) },
                        categories = categories,
                    )
                }
        }
    }

    private fun sortExpenses(expenses: List<Expense>, sort: TransactionSort): List<Expense> {
        return when (sort) {
            TransactionSort.DateDesc -> expenses.sortedByDescending { it.expenseDate }
            TransactionSort.DateAsc -> expenses.sortedBy { it.expenseDate }
            TransactionSort.AmountDesc -> expenses.sortedByDescending { it.amount.amountMinor }
            TransactionSort.AmountAsc -> expenses.sortedBy { it.amount.amountMinor }
        }
    }

    private fun Expense.toUiModel(categoriesById: Map<Long, Category>): TransactionItemUiModel {
        val category = categoriesById[categoryId]
        return TransactionItemUiModel(
            id = id,
            title = title,
            merchant = merchant,
            categoryName = category?.name ?: "Unknown",
            categoryColorHex = category?.colorHex ?: "#9CA3AF",
            amount = "-${amount.format()}",
            amountMinor = amount.amountMinor,
            dateLabel = formatDateLabel(expenseDate),
            expenseDateEpochDay = expenseDate.toEpochDay(),
        )
    }

    private fun formatDateLabel(date: LocalDate): String {
        val today = LocalDate.now()
        return when {
            date == today -> "Today"
            date == today.minusDays(1) -> "Yesterday"
            date.year == today.year -> date.format(SHORT_DATE_FORMAT)
            else -> date.format(FULL_DATE_FORMAT)
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_MS = 250L
        private val SHORT_DATE_FORMAT = DateTimeFormatter.ofPattern("MMM d")
        private val FULL_DATE_FORMAT = DateTimeFormatter.ofPattern("MMM d, yyyy")
    }
}
