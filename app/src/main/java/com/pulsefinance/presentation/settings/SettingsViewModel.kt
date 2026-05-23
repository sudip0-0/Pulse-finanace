package com.pulsefinance.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulsefinance.domain.model.Budget
import com.pulsefinance.domain.model.DomainResult
import com.pulsefinance.domain.model.Money
import com.pulsefinance.domain.repository.BudgetRepository
import com.pulsefinance.domain.repository.ExpenseRepository
import com.pulsefinance.domain.usecase.ExportTransactionsCsvUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val expenseRepository: ExpenseRepository,
    private val exportCsvUseCase: ExportTransactionsCsvUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    init {
        loadBudget()
    }

    private fun loadBudget() {
        viewModelScope.launch {
            val currentMonth = YearMonth.now()
            val budget = budgetRepository.getBudgetForMonth(currentMonth)
            if (budget != null) {
                val amountMajor = budget.amount.amountMinor / Money.MINOR_UNITS_PER_MAJOR
                val amountMinor = budget.amount.amountMinor % Money.MINOR_UNITS_PER_MAJOR
                val text = if (amountMinor == 0L) "$amountMajor" else "$amountMajor.${amountMinor.toString().padStart(2, '0')}"
                _uiState.value = _uiState.value.copy(
                    budgetAmountText = text,
                    budgetDisplayLabel = budget.amount.format(),
                )
            }
        }
    }

    fun onBudgetAmountChanged(text: String) {
        val filtered = text.filter { it.isDigit() || it == '.' }
        val sanitized = if (filtered.count { it == '.' } > 1) {
            _uiState.value.budgetAmountText
        } else {
            filtered.take(MAX_AMOUNT_LENGTH)
        }
        _uiState.value = _uiState.value.copy(budgetAmountText = sanitized, errorMessage = null)
    }

    fun onSaveBudget() {
        val amountMinor = parseAmountMinor(_uiState.value.budgetAmountText)
        if (amountMinor == null || amountMinor <= 0) {
            _uiState.value = _uiState.value.copy(errorMessage = "Enter a valid budget amount.")
            return
        }

        viewModelScope.launch {
            try {
                val now = Instant.now()
                val currentMonth = YearMonth.now()
                val budget = Budget(
                    month = currentMonth,
                    amount = Money(amountMinor),
                    createdAt = now,
                    updatedAt = now,
                )
                budgetRepository.saveBudget(budget)
                _uiState.value = _uiState.value.copy(
                    budgetDisplayLabel = Money(amountMinor).format(),
                    budgetSaved = true,
                    errorMessage = null,
                )
            } catch (error: Throwable) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = error.message ?: "Failed to save budget.",
                )
            }
        }
    }

    fun onBudgetDialogDismissed() {
        _uiState.value = _uiState.value.copy(budgetSaved = false)
    }

    fun onExportCsv() {
        if (_uiState.value.exportState is ExportState.Exporting) return
        _uiState.value = _uiState.value.copy(exportState = ExportState.Exporting)
        viewModelScope.launch {
            val startOfMonth = YearMonth.now().atDay(1)
            val endOfMonth = YearMonth.now().atEndOfMonth()
            val expenses = expenseRepository.observeExpensesBetween(startOfMonth, endOfMonth).first()
            when (val result = exportCsvUseCase(expenses)) {
                is DomainResult.Success -> {
                    _uiState.value = _uiState.value.copy(exportState = ExportState.Ready(result.value))
                }
                is DomainResult.Failure -> {
                    _uiState.value = _uiState.value.copy(
                        exportState = ExportState.Error(result.error.message),
                    )
                }
            }
        }
    }

    fun onExportComplete() {
        _uiState.value = _uiState.value.copy(exportState = ExportState.Idle)
    }

    private fun parseAmountMinor(text: String): Long? {
        if (text.isBlank()) return null
        val parts = text.split(".")
        val major = parts[0].toLongOrNull() ?: return null
        val minor = when {
            parts.size == 1 -> 0L
            parts[1].isEmpty() -> 0L
            parts[1].length == 1 -> parts[1].toLongOrNull()?.times(10) ?: return null
            else -> parts[1].take(2).toLongOrNull() ?: return null
        }
        return major * Money.MINOR_UNITS_PER_MAJOR + minor
    }

    companion object {
        private const val MAX_AMOUNT_LENGTH = 12
    }
}
