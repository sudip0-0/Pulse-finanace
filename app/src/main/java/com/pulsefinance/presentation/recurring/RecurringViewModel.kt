package com.pulsefinance.presentation.recurring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulsefinance.domain.model.RecurringFrequency
import com.pulsefinance.domain.model.RecurringRule
import com.pulsefinance.domain.repository.CategoryRepository
import com.pulsefinance.domain.repository.RecurringRuleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
class RecurringViewModel @Inject constructor(
    private val recurringRuleRepository: RecurringRuleRepository,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecurringUiState())
    val uiState: StateFlow<RecurringUiState> = _uiState

    init {
        observeRules()
    }

    private fun observeRules() {
        viewModelScope.launch {
            recurringRuleRepository.observeAllRules()
                .catch { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to load recurring rules.",
                    )
                }
                .collect { rules ->
                    val categories = categoryRepository.observeCategories().first()
                    val categoryMap = categories.associateBy { it.id }
                    _uiState.value = RecurringUiState(
                        isLoading = false,
                        rules = rules.map { rule ->
                            val category = categoryMap[rule.categoryId]
                            RecurringRuleUiModel(
                                id = rule.id,
                                title = rule.title,
                                merchant = rule.merchant,
                                amount = rule.amount.format(),
                                frequency = rule.frequency,
                                frequencyLabel = formatFrequency(rule.frequency, rule.interval),
                                nextDueDateLabel = formatNextDueDate(rule.nextDueDate),
                                categoryName = category?.name ?: "Other",
                                categoryColorHex = category?.colorHex ?: "#9CA3AF",
                                isActive = rule.isActive,
                            )
                        },
                    )
                }
        }
    }

    fun onTogglePause(ruleId: Long) {
        viewModelScope.launch {
            val rule = recurringRuleRepository.getRuleById(ruleId) ?: return@launch
            val now = Instant.now()
            val today = LocalDate.now()
            if (!rule.isActive && rule.nextDueDate.isBefore(today)) {
                // Resuming: advance nextDueDate to avoid generating back-dated expenses
                var next = rule.nextDueDate
                while (next.isBefore(today)) {
                    next = rule.nextDateAfter(next)
                }
                recurringRuleRepository.updateRule(
                    rule.copy(isActive = true, nextDueDate = next, updatedAt = now),
                )
            } else {
                recurringRuleRepository.updateRule(
                    rule.copy(isActive = !rule.isActive, updatedAt = now),
                )
            }
        }
    }

    fun onDeleteRule(ruleId: Long) {
        viewModelScope.launch {
            recurringRuleRepository.deleteRule(ruleId)
        }
    }

    private fun formatFrequency(frequency: RecurringFrequency, interval: Int): String {
        if (interval == 1) {
            return when (frequency) {
                RecurringFrequency.Weekly -> "Weekly"
                RecurringFrequency.Monthly -> "Monthly"
                RecurringFrequency.Yearly -> "Yearly"
            }
        }
        return when (frequency) {
            RecurringFrequency.Weekly -> "Every $interval weeks"
            RecurringFrequency.Monthly -> "Every $interval months"
            RecurringFrequency.Yearly -> "Every $interval years"
        }
    }

    private fun formatNextDueDate(date: LocalDate): String {
        val today = LocalDate.now()
        return when {
            date == today -> "Due today"
            date.isBefore(today) -> "Overdue"
            date == today.plusDays(1) -> "Due tomorrow"
            else -> "Due ${date.format(DATE_FORMAT)}"
        }
    }

    companion object {
        private val DATE_FORMAT = DateTimeFormatter.ofPattern("MMM d, yyyy")
    }
}
