package com.pulsefinance.presentation.recurring

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pulsefinance.domain.model.RecurringFrequency
import com.pulsefinance.presentation.common.theme.PulseColors
import com.pulsefinance.presentation.common.theme.PulseSpacing

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddRecurringRuleScreen(
    onBack: () -> Unit,
    viewModel: AddRecurringRuleViewModel = hiltViewModel(),
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
            title = { Text(text = if (state.isEditing) "Edit Recurring Rule" else "Add Recurring Rule") },
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
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.headlineMedium,
                colors = pulseTextFieldColors(),
            )

            // Title
            OutlinedTextField(
                value = state.title,
                onValueChange = viewModel::onTitleChanged,
                label = { Text("Title") },
                placeholder = { Text("e.g. Room rent, WorldLink internet") },
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
                placeholder = { Text("e.g. WorldLink, NEA, Ncell") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = pulseTextFieldColors(),
            )

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

            // Frequency selector
            Text(text = "Frequency", color = PulseColors.TextSecondary, style = MaterialTheme.typography.bodyLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(PulseSpacing.xs)) {
                RecurringFrequency.entries.forEach { frequency ->
                    val selected = state.selectedFrequency == frequency
                    FilterChip(
                        selected = selected,
                        onClick = { viewModel.onFrequencySelected(frequency) },
                        label = { Text(text = frequencyLabel(frequency)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PulseColors.Primary.copy(alpha = 0.2f),
                            selectedLabelColor = PulseColors.Primary,
                            containerColor = PulseColors.Surface,
                            labelColor = PulseColors.TextSecondary,
                        ),
                    )
                }
            }

            // Interval
            if (state.interval > 1 || state.selectedFrequency != RecurringFrequency.Monthly) {
                OutlinedTextField(
                    value = state.interval.toString(),
                    onValueChange = { text ->
                        text.toIntOrNull()?.let { viewModel.onIntervalChanged(it) }
                    },
                    label = { Text("Every N ${frequencyUnit(state.selectedFrequency)}") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next,
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = pulseTextFieldColors(),
                )
            }

            // Start date
            var showStartDatePicker by remember { mutableStateOf(false) }
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = state.startDateText,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Start date") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = pulseTextFieldColors(),
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { showStartDatePicker = true },
                )
            }
            if (showStartDatePicker) {
                val datePickerState = rememberDatePickerState()
                DatePickerDialog(
                    onDismissRequest = { showStartDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                viewModel.onStartDateSelected(millisToEpochDay(millis))
                            }
                            showStartDatePicker = false
                        }) { Text("OK") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showStartDatePicker = false }) { Text("Cancel") }
                    },
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            // End date (optional)
            var showEndDatePicker by remember { mutableStateOf(false) }
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = state.endDateText,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("End date (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = pulseTextFieldColors(),
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { showEndDatePicker = true },
                )
            }
            if (showEndDatePicker) {
                val datePickerState = rememberDatePickerState()
                DatePickerDialog(
                    onDismissRequest = { showEndDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                viewModel.onEndDateSelected(millisToEpochDay(millis))
                            }
                            showEndDatePicker = false
                        }) { Text("OK") }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            viewModel.onEndDateSelected(null)
                            showEndDatePicker = false
                        }) { Text("No end date") }
                    },
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            // Nepal-specific suggestions
            Text(
                text = "Common: Rent, WorldLink, NEA electricity, Khanepani, NTC/Ncell, School fee",
                style = MaterialTheme.typography.bodySmall,
                color = PulseColors.TextMuted,
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
                Text(
                    text = if (state.isSaving) "Saving..." else if (state.isEditing) "Update rule" else "Save rule",
                )
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

private fun frequencyLabel(frequency: RecurringFrequency): String = when (frequency) {
    RecurringFrequency.Weekly -> "Weekly"
    RecurringFrequency.Monthly -> "Monthly"
    RecurringFrequency.Yearly -> "Yearly"
}

private fun frequencyUnit(frequency: RecurringFrequency): String = when (frequency) {
    RecurringFrequency.Weekly -> "weeks"
    RecurringFrequency.Monthly -> "months"
    RecurringFrequency.Yearly -> "years"
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
