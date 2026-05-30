package com.pulsefinance.presentation.expense

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pulsefinance.domain.model.Category
import com.pulsefinance.domain.model.PaymentMethod
import com.pulsefinance.presentation.common.theme.PulseColors
import com.pulsefinance.presentation.common.theme.PulseSpacing

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddExpenseScreen(
    onBack: () -> Unit,
    onScanReceipt: () -> Unit = {},
    viewModel: AddExpenseViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.saved) {
        if (state.saved) onBack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PulseColors.Background)
            .verticalScroll(rememberScrollState()),
    ) {
        TopAppBar(
            title = { Text(text = if (state.isEditing) "Edit Expense" else "Add Expense") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = PulseColors.Background),
        )

        Column(
            modifier = Modifier.padding(horizontal = PulseSpacing.xl),
            verticalArrangement = Arrangement.spacedBy(PulseSpacing.lg),
        ) {
            if (!state.isEditing) {
                OutlinedButton(
                    onClick = onScanReceipt,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(
                        imageVector = Icons.Default.DocumentScanner,
                        contentDescription = null,
                        tint = PulseColors.Primary,
                    )
                    Spacer(modifier = Modifier.size(PulseSpacing.sm))
                    Text("Scan receipt", color = PulseColors.Primary)
                }
            }

            // Amount input
            OutlinedTextField(
                value = state.amountText,
                onValueChange = viewModel::onAmountChanged,
                label = { Text("Amount") },
                prefix = { Text("रू ") },
                placeholder = { Text("0.00") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next,
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "Expense amount in NPR" },
                textStyle = MaterialTheme.typography.headlineMedium,
                colors = pulseTextFieldColors(),
            )

            // Title
            OutlinedTextField(
                value = state.title,
                onValueChange = viewModel::onTitleChanged,
                label = { Text("Title") },
                placeholder = { Text("e.g. Pathao ride, Lunch") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = pulseTextFieldColors(),
            )

            // Merchant
            OutlinedTextField(
                value = state.merchant,
                onValueChange = viewModel::onMerchantChanged,
                label = { Text("Merchant (optional)") },
                placeholder = { Text("e.g. Pathao, Daraz, Bhat-Bhateni") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = pulseTextFieldColors(),
            )

            // Category suggestion
            if (state.suggestedCategory != null && state.selectedCategory != state.suggestedCategory) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(PulseColors.SurfaceHigh)
                        .clickable { viewModel.onAcceptSuggestion() }
                        .padding(PulseSpacing.sm)
                        .semantics {
                            contentDescription = "Suggested category: ${state.suggestedCategory!!.name}. Tap to accept."
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(PulseSpacing.xs),
                ) {
                    CategoryDot(color = parseColor(state.suggestedCategory!!.colorHex))
                    Text(
                        text = "Suggested: ${state.suggestedCategory!!.name}",
                        color = PulseColors.TextSecondary,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    if (state.suggestionReason != null) {
                        Text(
                            text = "(${state.suggestionReason})",
                            color = PulseColors.TextMuted,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }

            // Category selector
            Text(text = "Category", color = PulseColors.TextSecondary, style = MaterialTheme.typography.bodyLarge)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(PulseSpacing.xs),
                verticalArrangement = Arrangement.spacedBy(PulseSpacing.xs),
            ) {
                state.categories.forEach { category ->
                    val selected = state.selectedCategory?.id == category.id
                    FilterChip(
                        selected = selected,
                        onClick = { viewModel.onCategorySelected(category) },
                        label = { Text(text = category.name) },
                        leadingIcon = if (selected) {
                            { CategoryDot(color = parseColor(category.colorHex)) }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PulseColors.SurfaceHigh,
                            selectedLabelColor = PulseColors.TextPrimary,
                            containerColor = PulseColors.Surface,
                            labelColor = PulseColors.TextSecondary,
                        ),
                    )
                }
            }

            // Payment method
            Text(text = "Payment method", color = PulseColors.TextSecondary, style = MaterialTheme.typography.bodyLarge)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(PulseSpacing.xs),
                verticalArrangement = Arrangement.spacedBy(PulseSpacing.xs),
            ) {
                PaymentMethod.entries.forEach { method ->
                    val selected = state.selectedPaymentMethod == method
                    FilterChip(
                        selected = selected,
                        onClick = { viewModel.onPaymentMethodSelected(method) },
                        label = { Text(text = paymentMethodLabel(method)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PulseColors.Primary.copy(alpha = 0.2f),
                            selectedLabelColor = PulseColors.Primary,
                            containerColor = PulseColors.Surface,
                            labelColor = PulseColors.TextSecondary,
                        ),
                    )
                }
            }

            // Date
            var showDatePicker by remember { mutableStateOf(false) }
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = state.selectedDateText,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Date") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = pulseTextFieldColors(),
                )
                // Transparent overlay to capture clicks on the read-only field
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { showDatePicker = true },
                )
            }
            if (showDatePicker) {
                val datePickerState = rememberDatePickerState()
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val epochDay = millisToEpochDay(millis)
                                viewModel.onDateSelected(epochDay)
                            }
                            showDatePicker = false
                        }) { Text("OK") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
                    },
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            // Note
            OutlinedTextField(
                value = state.note,
                onValueChange = viewModel::onNoteChanged,
                label = { Text("Note (optional)") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                maxLines = 3,
                modifier = Modifier.fillMaxWidth(),
                colors = pulseTextFieldColors(),
            )

            // Error message
            if (state.errorMessage != null) {
                Text(
                    text = state.errorMessage!!,
                    color = PulseColors.Danger,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            // Save button
            Button(
                onClick = viewModel::onSave,
                enabled = !state.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PulseColors.Primary),
            ) {
                Text(text = if (state.isSaving) "Saving..." else if (state.isEditing) "Update expense" else "Save expense")
            }

            Spacer(modifier = Modifier.height(PulseSpacing.xl))
        }
    }
}

@Composable
private fun CategoryDot(color: Color) {
    Box(
        modifier = Modifier
            .size(12.dp)
            .clip(CircleShape)
            .background(color),
    )
}

@Composable
private fun pulseTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = PulseColors.Primary,
    unfocusedBorderColor = PulseColors.SurfaceHigh,
    focusedLabelColor = PulseColors.Primary,
    unfocusedLabelColor = PulseColors.TextSecondary,
    cursorColor = PulseColors.Primary,
    focusedTextColor = PulseColors.TextPrimary,
    unfocusedTextColor = PulseColors.TextPrimary,
    focusedPlaceholderColor = PulseColors.TextMuted,
    unfocusedPlaceholderColor = PulseColors.TextMuted,
    focusedPrefixColor = PulseColors.TextSecondary,
    unfocusedPrefixColor = PulseColors.TextSecondary,
)

private fun paymentMethodLabel(method: PaymentMethod): String = when (method) {
    PaymentMethod.Cash -> "Cash"
    PaymentMethod.Esewa -> "eSewa"
    PaymentMethod.Khalti -> "Khalti"
    PaymentMethod.Fonepay -> "Fonepay"
    PaymentMethod.Bank -> "Bank"
    PaymentMethod.Card -> "Card"
}

private fun parseColor(hex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (_: Exception) {
        PulseColors.Other
    }
}

// Material3 DatePicker reports the selected day as midnight UTC. Convert through UTC
// so the user sees the calendar date they actually tapped, regardless of device timezone.
private fun millisToEpochDay(utcMillis: Long): Long =
    java.time.Instant.ofEpochMilli(utcMillis)
        .atZone(java.time.ZoneOffset.UTC)
        .toLocalDate()
        .toEpochDay()
