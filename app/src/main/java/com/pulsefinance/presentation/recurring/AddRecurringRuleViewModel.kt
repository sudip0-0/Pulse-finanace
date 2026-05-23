package com.pulsefinance.presentation.recurring

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulsefinance.domain.model.Category
import com.pulsefinance.domain.model.Money
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
class AddRecurringRuleViewModel @Inject constructor(
    private val recurringRuleRepository: RecurringRuleRepository,
    private val categoryRepository: CategoryRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddRecurringRuleUiState())
    val uiState: StateFlow<AddRecurringRuleUiState> = _uiState

    private var editingRuleId: Long? = null

    init {
        loadCategories()
        val ruleId = savedStateHandle.get<Long>("ruleId")
        if (ruleId != null && ruleId > 0) {
            loadRuleForEdit(ruleId)
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            val categories = categoryRepository.observeCategories().first()
            _uiState.value = _uiState.value.copy(categories = categories)
        }
    }

    private fun loadRuleForEdit(ruleId: Long) {
        viewModelScope.launch {
            val rule = recurringRuleRepository.getRuleById(ruleId) ?: return@launch
            editingRuleId = ruleId
            val categories = _uiState.value.categories.ifEmpty {
                categoryRepository.observeCategories().first()
            }
            val category = categories.firstOrNull { it.id == rule.categoryId }
            val amountMajor = rule.amount.amountMinor / Money.MINOR_UNITS_PER_MAJOR
            val amountMinor = rule.amount.amountMinor % Money.MINOR_UNITS_PER_MAJOR
            val amountText = if (amountMinor == 0L) "$amountMajor" else "$amountMajor.${amountMinor.toString().padStart(2, '0')}"
            _uiState.value = _uiState.value.copy(
                amountText = amountText,
                title = rule.title,
                merchant = rule.merchant.orEmpty(),
                selectedCategory = category,
                categories = categories,
                selectedFrequency = rule.frequency,
                interval = rule.interval,
                startDateText = formatDateLabel(rule.startDate),
                startDateEpochDay = rule.startDate.toEpochDay(),
                endDateText = rule.endDate?.let { formatDateLabel(it) } ?: "No end date",
                endDateEpochDay = rule.endDate?.toEpochDay(),
                isEditing = true,
            )
        }
    }

    fun onAmountChanged(text: String) {
        val filtered = text.filter { it.isDigit() || it == '.' }
        val sanitized = if (filtered.count { it == '.' } > 1) {
            _uiState.value.amountText
        } else {
            filtered.take(MAX_AMOUNT_LENGTH)
        }
        _uiState.value = _uiState.value.copy(amountText = sanitized, errorMessage = null)
    }

    fun onTitleChanged(text: String) {
        _uiState.value = _uiState.value.copy(title = text, errorMessage = null)
    }

    fun onMerchantChanged(text: String) {
        _uiState.value = _uiState.value.copy(merchant = text, errorMessage = null)
    }

    fun onCategorySelected(category: Category) {
        _uiState.value = _uiState.value.copy(selectedCategory = category, errorMessage = null)
    }

    fun onFrequencySelected(frequency: RecurringFrequency) {
        _uiState.value = _uiState.value.copy(selectedFrequency = frequency)
    }

    fun onIntervalChanged(interval: Int) {
        if (interval in 1..52) {
            _uiState.value = _uiState.value.copy(interval = interval)
        }
    }

    fun onStartDateSelected(epochDay: Long) {
        val date = LocalDate.ofEpochDay(epochDay)
        _uiState.value = _uiState.value.copy(
            startDateText = formatDateLabel(date),
            startDateEpochDay = epochDay,
            errorMessage = null,
        )
    }

    fun onEndDateSelected(epochDay: Long?) {
        if (epochDay == null) {
            _uiState.value = _uiState.value.copy(endDateText = "No end date", endDateEpochDay = null)
        } else {
            val date = LocalDate.ofEpochDay(epochDay)
            _uiState.value = _uiState.value.copy(
                endDateText = formatDateLabel(date),
                endDateEpochDay = epochDay,
                errorMessage = null,
            )
        }
    }

    fun onSave() {
        val state = _uiState.value
        if (state.isSaving) return

        val amountMinor = parseAmountMinor(state.amountText)
        if (amountMinor == null || amountMinor <= 0) {
            _uiState.value = state.copy(errorMessage = "Enter a valid amount.")
            return
        }
        val title = state.title.ifBlank { state.merchant }.ifBlank { null }
        if (title == null) {
            _uiState.value = state.copy(errorMessage = "Title or merchant is required.")
            return
        }
        val category = state.selectedCategory
        if (category == null) {
            _uiState.value = state.copy(errorMessage = "Select a category.")
            return
        }
        val startDate = state.startDateEpochDay?.let { LocalDate.ofEpochDay(it) } ?: LocalDate.now()
        val endDate = state.endDateEpochDay?.let { LocalDate.ofEpochDay(it) }
        if (endDate != null && endDate.isBefore(startDate)) {
            _uiState.value = state.copy(errorMessage = "End date cannot be before start date.")
            return
        }

        _uiState.value = state.copy(isSaving = true, errorMessage = null)

        viewModelScope.launch {
            try {
                val now = Instant.now()
                val existingId = editingRuleId
                if (existingId != null) {
                    val existing = recurringRuleRepository.getRuleById(existingId)
                    val rule = RecurringRule(
                        id = existingId,
                        title = title,
                        merchant = state.merchant.ifBlank { null },
                        amount = Money(amountMinor),
                        categoryId = category.id,
                        frequency = state.selectedFrequency,
                        interval = state.interval,
                        startDate = startDate,
                        nextDueDate = existing?.nextDueDate ?: startDate,
                        endDate = endDate,
                        isActive = existing?.isActive ?: true,
                        createdAt = existing?.createdAt ?: now,
                        updatedAt = now,
                    )
                    recurringRuleRepository.updateRule(rule)
                } else {
                    val rule = RecurringRule(
                        title = title,
                        merchant = state.merchant.ifBlank { null },
                        amount = Money(amountMinor),
                        categoryId = category.id,
                        frequency = state.selectedFrequency,
                        interval = state.interval,
                        startDate = startDate,
                        nextDueDate = startDate,
                        endDate = endDate,
                        isActive = true,
                        createdAt = now,
                        updatedAt = now,
                    )
                    recurringRuleRepository.addRule(rule)
                }
                _uiState.value = _uiState.value.copy(isSaving = false, saved = true)
            } catch (error: Throwable) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = error.message ?: "Failed to save recurring rule.",
                )
            }
        }
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

    private fun formatDateLabel(date: LocalDate): String {
        val today = LocalDate.now()
        return when (date) {
            today -> "Today"
            today.minusDays(1) -> "Yesterday"
            today.plusDays(1) -> "Tomorrow"
            else -> date.format(DATE_FORMAT)
        }
    }

    companion object {
        private const val MAX_AMOUNT_LENGTH = 12
        private val DATE_FORMAT = DateTimeFormatter.ofPattern("MMM d, yyyy")
    }
}
