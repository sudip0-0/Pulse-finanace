package com.pulsefinance.presentation.settings

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pulsefinance.presentation.common.components.PulseCard
import com.pulsefinance.presentation.common.theme.PulseColors
import com.pulsefinance.presentation.common.theme.PulseSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onRecurringClick: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showBudgetDialog by remember { mutableStateOf(false) }

    // CSV export via document creation API (no storage permissions needed)
    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv"),
    ) { uri: Uri? ->
        if (uri != null) {
            val exportState = state.exportState
            if (exportState is ExportState.Ready) {
                writeCsvToUri(context, uri, exportState.csvContent)
                Toast.makeText(context, "Exported successfully", Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.onExportComplete()
    }

    // Launch document picker when CSV is ready
    LaunchedEffect(state.exportState) {
        if (state.exportState is ExportState.Ready) {
            val fileName = "pulse_expenses_${java.time.YearMonth.now()}.csv"
            createDocumentLauncher.launch(fileName)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PulseColors.Background)
            .verticalScroll(rememberScrollState()),
    ) {
        TopAppBar(
            title = { Text("Settings") },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = PulseColors.Background),
        )

        Column(
            modifier = Modifier.padding(horizontal = PulseSpacing.xl),
            verticalArrangement = Arrangement.spacedBy(PulseSpacing.sm),
        ) {
            // Profile block
            PulseCard {
                Text(text = "Aayush Shrestha", style = MaterialTheme.typography.titleMedium)
                Text(text = "aayush@pulse.local", color = PulseColors.TextSecondary)
            }

            Spacer(modifier = Modifier.height(PulseSpacing.xs))

            // Monthly budget
            SettingRow(
                title = "Monthly budget",
                value = state.budgetDisplayLabel,
                onClick = { showBudgetDialog = true },
            )

            // CSV export
            SettingRow(
                title = "Export transactions",
                value = when (state.exportState) {
                    is ExportState.Exporting -> "Preparing..."
                    is ExportState.Error -> "Export failed"
                    else -> "CSV · UTF-8 · This month"
                },
                onClick = { viewModel.onExportCsv() },
            )

            // Currency
            SettingRow(
                title = "Currency",
                value = state.currencyLabel,
            )

            // Notifications
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PulseCard(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column {
                            Text(text = "Notifications", style = MaterialTheme.typography.titleMedium)
                            Text(text = "Coming soon", color = PulseColors.TextMuted)
                        }
                        Switch(
                            checked = state.notificationsEnabled,
                            onCheckedChange = viewModel::onNotificationsToggled,
                            colors = SwitchDefaults.colors(checkedTrackColor = PulseColors.Primary),
                            enabled = false,
                        )
                    }
                }
            }

            // Recurring expenses
            SettingRow(
                title = "Recurring expenses",
                value = "Manage recurring bills",
                onClick = onRecurringClick,
            )

            // Categories
            SettingRow(
                title = "Categories",
                value = "View default categories",
            )

            Spacer(modifier = Modifier.height(PulseSpacing.xl))
        }
    }

    // Budget editor dialog
    if (showBudgetDialog) {
        AlertDialog(
            onDismissRequest = { showBudgetDialog = false },
            title = { Text("Monthly Budget") },
            text = {
                Column {
                    Text(
                        text = "Set your monthly spending limit for ${java.time.YearMonth.now().month.name.lowercase().replaceFirstChar { it.uppercase() }}",
                        color = PulseColors.TextSecondary,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(modifier = Modifier.height(PulseSpacing.md))
                    OutlinedTextField(
                        value = state.budgetAmountText,
                        onValueChange = viewModel::onBudgetAmountChanged,
                        label = { Text("Amount") },
                        prefix = { Text("रू ") },
                        placeholder = { Text("e.g. 35000") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Done,
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PulseColors.Primary,
                            unfocusedBorderColor = PulseColors.SurfaceHigh,
                            focusedLabelColor = PulseColors.Primary,
                            unfocusedLabelColor = PulseColors.TextSecondary,
                            cursorColor = PulseColors.Primary,
                        ),
                    )
                    if (state.errorMessage != null) {
                        Spacer(modifier = Modifier.height(PulseSpacing.xs))
                        Text(
                            text = state.errorMessage!!,
                            color = PulseColors.Danger,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onSaveBudget()
                    if (state.errorMessage == null) {
                        showBudgetDialog = false
                    }
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showBudgetDialog = false }) { Text("Cancel") }
            },
        )
    }
}

@Composable
private fun SettingRow(
    title: String,
    value: String,
    onClick: (() -> Unit)? = null,
) {
    val rowModifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier

    PulseCard(modifier = rowModifier) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Text(text = value, color = PulseColors.TextSecondary, style = MaterialTheme.typography.bodyMedium)
    }
}

private fun writeCsvToUri(context: Context, uri: Uri, csvContent: String) {
    context.contentResolver.openOutputStream(uri)?.use { outputStream ->
        // Write UTF-8 BOM for Excel compatibility with Nepali Unicode
        outputStream.write(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()))
        outputStream.write(csvContent.toByteArray(Charsets.UTF_8))
    }
}
