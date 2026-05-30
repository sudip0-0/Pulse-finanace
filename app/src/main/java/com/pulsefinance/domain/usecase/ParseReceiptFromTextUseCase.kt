package com.pulsefinance.domain.usecase

import com.pulsefinance.domain.receipt.ReceiptDraft
import com.pulsefinance.domain.receipt.ReceiptTextParser
import java.time.LocalDate

class ParseReceiptFromTextUseCase {
    operator fun invoke(rawText: String, today: LocalDate = LocalDate.now()): ReceiptDraft {
        return ReceiptTextParser.parse(rawText, today)
    }
}
