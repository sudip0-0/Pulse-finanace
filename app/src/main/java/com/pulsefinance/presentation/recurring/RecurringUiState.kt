package com.pulsefinance.presentation.recurring

import com.pulsefinance.domain.model.RecurringFrequency

data class RecurringUiState(
    val isLoading: Boolean = true,
    val rules: List<RecurringRuleUiModel> = emptyList(),
    val errorMessage: String? = null,
) {
    val isEmpty: Boolean
        get() = !isLoading && errorMessage == null && rules.isEmpty()
}

data class RecurringRuleUiModel(
    val id: Long,
    val title: String,
    val merchant: String?,
    val amount: String,
    val frequency: RecurringFrequency,
    val frequencyLabel: String,
    val nextDueDateLabel: String,
    val categoryName: String,
    val categoryColorHex: String,
    val isActive: Boolean,
)
