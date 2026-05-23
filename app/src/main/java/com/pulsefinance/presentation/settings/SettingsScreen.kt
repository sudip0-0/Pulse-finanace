package com.pulsefinance.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.pulsefinance.presentation.common.components.PulseCard
import com.pulsefinance.presentation.common.theme.PulseColors
import com.pulsefinance.presentation.common.theme.PulseSpacing

@Composable
fun SettingsScreen(onRecurringClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PulseColors.Background)
            .padding(PulseSpacing.xl),
        verticalArrangement = Arrangement.spacedBy(PulseSpacing.lg),
    ) {
        Text(text = "Settings", style = MaterialTheme.typography.titleLarge)
        PulseCard {
            Text(text = "Aayush Shrestha", style = MaterialTheme.typography.titleMedium)
            Text(text = "aayush@pulse.local", color = PulseColors.TextSecondary)
        }
        SettingRow("Monthly budget", "NPR 3,500.00")
        SettingRow("CSV export", "UTF-8 export placeholder")
        SettingRow("Currency", "NPR")
        SettingRow("Recurring expenses", "WorldLink, rent, NEA")
    }
}

@Composable
private fun SettingRow(title: String, value: String) {
    PulseCard {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Text(text = value, color = PulseColors.TextSecondary)
    }
}
