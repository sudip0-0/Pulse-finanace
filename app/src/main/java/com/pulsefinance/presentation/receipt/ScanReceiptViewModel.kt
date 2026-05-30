package com.pulsefinance.presentation.receipt

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulsefinance.domain.model.DomainResult
import com.pulsefinance.domain.model.Expense
import com.pulsefinance.domain.model.Money
import com.pulsefinance.domain.model.PaymentMethod
import com.pulsefinance.domain.repository.CategoryRepository
import com.pulsefinance.domain.repository.ReceiptTextRecognizer
import com.pulsefinance.domain.usecase.AddExpenseUseCase
import com.pulsefinance.domain.usecase.CategorizationInput
import com.pulsefinance.domain.usecase.CategorizeExpenseUseCase
import com.pulsefinance.domain.usecase.ParseReceiptFromTextUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
class ScanReceiptViewModel @Inject constructor(
    private val receiptTextRecognizer: ReceiptTextRecognizer,
    private val parseReceiptFromText: ParseReceiptFromTextUseCase,
    private val categorizeExpenseUseCase: CategorizeExpenseUseCase,
    private val addExpenseUseCase: AddExpenseUseCase,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScanReceiptUiState())
    val uiState: StateFlow<ScanReceiptUiState> = _uiState

    private var categorizationJob: Job? = null

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            val categories = categoryRepository.observeCategories().first()
            _uiState.value = _uiState.value.copy(categories = categories)
        }
    }

    fun onImageSelected(bitmap: Bitmap) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                step = ScanReceiptStep.Processing,
                errorMessage = null,
                processingMessage = "Reading receipt…",
            )
            when (val ocrResult = receiptTextRecognizer.recognizeText(bitmap.toReceiptImageInput())) {
                is DomainResult.Failure -> {
                    _uiState.value = _uiState.value.copy(
                        step = ScanReceiptStep.Capture,
                        errorMessage = ocrResult.error.message,
                    )
                }
                is DomainResult.Success -> applyParsedReceipt(ocrResult.value)
            }
        }
    }

    internal fun onReceiptTextForTest(rawText: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(step = ScanReceiptStep.Processing)
            applyParsedReceipt(rawText)
        }
    }

    private suspend fun applyParsedReceipt(rawText: String) {
        val draft = parseReceiptFromText(rawText)
        val amountText = draft.amountMinor?.let { formatAmountText(it) }.orEmpty()
        val expenseDate = draft.expenseDate ?: LocalDate.now()
        _uiState.value = _uiState.value.copy(
            step = ScanReceiptStep.Review,
            amountText = amountText,
            title = draft.title.orEmpty(),
            merchant = draft.merchant.orEmpty(),
            note = draft.note.orEmpty(),
            selectedPaymentMethod = draft.paymentMethodHint ?: PaymentMethod.Cash,
            selectedDateText = formatDateLabel(expenseDate),
            selectedDateEpochDay = expenseDate.toEpochDay(),
            errorMessage = if (draft.amountMinor == null) {
                "Could not detect amount. Please enter it manually."
            } else {
                null
            },
        )
        triggerCategorization()
    }

    fun onScanAgain() {
        _uiState.value = ScanReceiptUiState(categories = _uiState.value.categories)
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
        triggerCategorization()
    }

    fun onMerchantChanged(text: String) {
        _uiState.value = _uiState.value.copy(merchant = text, errorMessage = null)
        triggerCategorization()
    }

    fun onNoteChanged(text: String) {
        _uiState.value = _uiState.value.copy(note = text)
    }

    fun onCategorySelected(category: com.pulsefinance.domain.model.Category) {
        _uiState.value = _uiState.value.copy(selectedCategory = category, errorMessage = null)
    }

    fun onPaymentMethodSelected(method: PaymentMethod) {
        _uiState.value = _uiState.value.copy(selectedPaymentMethod = method)
    }

    fun onDateSelected(epochDay: Long) {
        val date = LocalDate.ofEpochDay(epochDay)
        _uiState.value = _uiState.value.copy(
            selectedDateText = formatDateLabel(date),
            selectedDateEpochDay = epochDay,
            errorMessage = null,
        )
    }

    fun onAcceptSuggestion() {
        val suggested = _uiState.value.suggestedCategory ?: return
        _uiState.value = _uiState.value.copy(selectedCategory = suggested)
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
        val expenseDate = state.selectedDateEpochDay?.let { LocalDate.ofEpochDay(it) } ?: LocalDate.now()

        _uiState.value = state.copy(isSaving = true, errorMessage = null)

        viewModelScope.launch {
            val now = Instant.now()
            val expense = Expense(
                title = title,
                merchant = state.merchant.ifBlank { null },
                amount = Money(amountMinor),
                categoryId = category.id,
                paymentMethod = state.selectedPaymentMethod,
                expenseDate = expenseDate,
                note = state.note.ifBlank { null },
                isRecurringGenerated = false,
                recurringRuleId = null,
                createdAt = now,
                updatedAt = now,
            )
            when (val result = addExpenseUseCase(expense)) {
                is DomainResult.Success -> {
                    _uiState.value = _uiState.value.copy(isSaving = false, saved = true)
                }
                is DomainResult.Failure -> {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        errorMessage = result.error.message,
                    )
                }
            }
        }
    }

    private fun triggerCategorization() {
        categorizationJob?.cancel()
        categorizationJob = viewModelScope.launch {
            delay(CATEGORIZATION_DEBOUNCE_MS)
            val state = _uiState.value
            if (state.title.isBlank() && state.merchant.isBlank()) {
                _uiState.value = state.copy(suggestedCategory = null, suggestionReason = null)
                return@launch
            }
            val input = CategorizationInput(
                title = state.title,
                merchant = state.merchant.ifBlank { null },
                note = state.note.ifBlank { null },
            )
            when (val result = categorizeExpenseUseCase(input)) {
                is DomainResult.Success -> {
                    val suggestion = result.value
                    _uiState.value = _uiState.value.copy(
                        suggestedCategory = suggestion.category,
                        suggestionReason = "Matched: ${suggestion.matchedText ?: suggestion.category.name}",
                    )
                    if (_uiState.value.selectedCategory == null) {
                        _uiState.value = _uiState.value.copy(selectedCategory = suggestion.category)
                    }
                }
                is DomainResult.Failure -> {
                    _uiState.value = _uiState.value.copy(suggestedCategory = null, suggestionReason = null)
                }
            }
        }
    }

    private fun formatAmountText(amountMinor: Long): String {
        val major = amountMinor / Money.MINOR_UNITS_PER_MAJOR
        val minor = amountMinor % Money.MINOR_UNITS_PER_MAJOR
        return if (minor == 0L) "$major" else "$major.${minor.toString().padStart(2, '0')}"
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
            else -> date.format(DATE_FORMAT)
        }
    }

    companion object {
        private const val CATEGORIZATION_DEBOUNCE_MS = 300L
        private const val MAX_AMOUNT_LENGTH = 12
        private val DATE_FORMAT = DateTimeFormatter.ofPattern("MMM d, yyyy")
    }
}
