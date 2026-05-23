package com.pulsefinance.presentation.settings

data class SettingsUiState(
    val budgetAmountText: String = "",
    val budgetDisplayLabel: String = "Not set",
    val budgetSaved: Boolean = false,
    val currencyLabel: String = "NPR (रू)",
    val notificationsEnabled: Boolean = false,
    val exportState: ExportState = ExportState.Idle,
    val errorMessage: String? = null,
)

sealed interface ExportState {
    data object Idle : ExportState
    data object Exporting : ExportState
    data class Ready(val csvContent: String) : ExportState
    data class Error(val message: String) : ExportState
}
