package com.pulsefinance.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulsefinance.domain.model.CategorySpend
import com.pulsefinance.domain.model.DashboardSnapshot
import com.pulsefinance.domain.model.Expense
import com.pulsefinance.domain.usecase.GenerateDueRecurringExpensesUseCase
import com.pulsefinance.domain.usecase.ObserveDashboardUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val observeDashboard: ObserveDashboardUseCase,
    private val generateDueRecurringExpenses: GenerateDueRecurringExpensesUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState

    private var currentMonth: YearMonth = YearMonth.now()

    init {
        generateRecurringExpenses()
        observeMonth(currentMonth)
    }

    private fun generateRecurringExpenses() {
        viewModelScope.launch {
            generateDueRecurringExpenses()
        }
    }

    private fun observeMonth(month: YearMonth) {
        currentMonth = month
        viewModelScope.launch {
            observeDashboard(month, RECENT_LIMIT)
                .onStart { _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null) }
                .catch { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Something went wrong.",
                    )
                }
                .collect { snapshot ->
                    _uiState.value = snapshot.toUiState()
                }
        }
    }

    private fun DashboardSnapshot.toUiState(): DashboardUiState {
        val totalMinor = monthlySpend.amountMinor
        return DashboardUiState(
            isLoading = false,
            errorMessage = null,
            monthLabel = formatMonthLabel(month),
            monthlySpend = monthlySpend.format(),
            budgetState = budgetProgress?.toUiState(),
            categorySpending = categorySpending.take(TOP_CATEGORIES).map { it.toUiModel(totalMinor) },
            recentTransactions = recentTransactions.map { it.toUiModel() },
            quickAddItems = QUICK_ADD_ITEMS,
        )
    }

    private fun CategorySpend.toUiModel(totalMinor: Long): CategorySpendUiModel {
        val pct = if (totalMinor > 0) (amount.amountMinor * 100 / totalMinor).toInt() else 0
        return CategorySpendUiModel(
            categoryId = category.id,
            name = category.name,
            amount = amount.format(),
            percent = "$pct%",
            colorHex = category.colorHex,
        )
    }

    private fun Expense.toUiModel(): TransactionUiModel {
        return TransactionUiModel(
            id = id,
            merchant = merchant ?: title,
            category = "",
            amount = "-${amount.format()}",
            dateLabel = formatDateLabel(expenseDate),
            colorHex = "",
        )
    }

    private fun formatMonthLabel(month: YearMonth): String {
        val monthName = month.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
        return "$monthName ${month.year}"
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
        private const val RECENT_LIMIT = 4
        private const val TOP_CATEGORIES = 3
        private val SHORT_DATE_FORMAT = DateTimeFormatter.ofPattern("MMM d")
        private val FULL_DATE_FORMAT = DateTimeFormatter.ofPattern("MMM d, yyyy")
        private val QUICK_ADD_ITEMS = listOf("Other", "Food", "Pathao", "Daraz", "Fuel", "NTC/Ncell", "eSewa/Khalti")
    }
}
