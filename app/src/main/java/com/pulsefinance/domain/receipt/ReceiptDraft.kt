package com.pulsefinance.domain.receipt

import com.pulsefinance.domain.model.PaymentMethod
import java.time.LocalDate

data class ReceiptDraft(
    val amountMinor: Long? = null,
    val merchant: String? = null,
    val title: String? = null,
    val expenseDate: LocalDate? = null,
    val paymentMethodHint: PaymentMethod? = null,
    val note: String? = null,
    val rawText: String = "",
)
