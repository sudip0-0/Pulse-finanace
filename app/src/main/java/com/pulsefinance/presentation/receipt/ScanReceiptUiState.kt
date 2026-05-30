package com.pulsefinance.presentation.receipt

import com.pulsefinance.domain.model.Category
import com.pulsefinance.domain.model.PaymentMethod

enum class ScanReceiptStep {
    Capture,
    Processing,
    Review,
}

data class ScanReceiptUiState(
    val step: ScanReceiptStep = ScanReceiptStep.Capture,
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
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val processingMessage: String = "Reading receipt…",
    val saved: Boolean = false,
)
