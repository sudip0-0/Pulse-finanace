package com.pulsefinance.domain.repository

import com.pulsefinance.domain.model.DomainError
import com.pulsefinance.domain.model.DomainResult

interface ReceiptTextRecognizer {
    suspend fun recognizeText(image: ReceiptImageInput): DomainResult<String>
}

data class ReceiptImageInput(
    val width: Int,
    val height: Int,
    val pixels: IntArray,
)
