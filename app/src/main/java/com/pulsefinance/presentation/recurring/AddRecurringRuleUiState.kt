package com.pulsefinance.presentation.recurring

import com.pulsefinance.domain.model.Category
import com.pulsefinance.domain.model.RecurringFrequency

data class AddRecurringRuleUiState(
    val amountText: String = "",
    val title: String = "",
    val merchant: String = "",
    val selectedCategory: Category? = null,
    val categories: List<Category> = emptyList(),
    val selectedFrequency: RecurringFrequency = RecurringFrequency.Monthly,
    val interval: Int = 1,
    val startDateText: String = "Today",
    val startDateEpochDay: Long? = null,
    val endDateText: String = "No end date",
    val endDateEpochDay: Long? = null,
    val isEditing: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val saved: Boolean = false,
)
