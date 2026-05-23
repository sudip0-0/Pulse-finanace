package com.pulsefinance.presentation.expense

import com.pulsefinance.domain.model.Category
import com.pulsefinance.domain.model.PaymentMethod

data class AddExpenseUiState(
    val amountText: String = "",
    val title: String = "",
    val merchant: String = "",
    val note: String = "",
    val selectedCategory: Category? = null,
    val suggestedCategory: Category? = null,
    val suggestionReason: String? = null,
    val categories: List<Category> = emptyList(),
    val selectedPaymentMethod: PaymentMethod = PaymentMethod.Cash,
    val selectedDateText: String = "Today",
    val selectedDateEpochDay: Long? = null,
    val isRecurring: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val saved: Boolean = false,
)
