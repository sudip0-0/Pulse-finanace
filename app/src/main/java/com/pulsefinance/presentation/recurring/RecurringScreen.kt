package com.pulsefinance.presentation.recurring

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
fun RecurringScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PulseColors.Background)
            .padding(PulseSpacing.xl),
        verticalArrangement = Arrangement.spacedBy(PulseSpacing.lg),
    ) {
        Text(text = "Recurring", style = MaterialTheme.typography.titleLarge)
        PulseCard {
            Text(text = "WorldLink internet", style = MaterialTheme.typography.titleMedium)
            Text(text = "Monthly - NPR 1,500.00", color = PulseColors.TextSecondary)
        }
        PulseCard {
            Text(text = "Room rent", style = MaterialTheme.typography.titleMedium)
            Text(text = "Monthly - NPR 18,000.00", color = PulseColors.TextSecondary)
        }
    }
}
