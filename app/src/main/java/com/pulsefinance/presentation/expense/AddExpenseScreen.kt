package com.pulsefinance.presentation.expense

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.pulsefinance.presentation.common.theme.PulseColors
import com.pulsefinance.presentation.common.theme.PulseSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PulseColors.Background)
            .padding(horizontal = PulseSpacing.xl),
        verticalArrangement = Arrangement.spacedBy(PulseSpacing.lg),
    ) {
        TopAppBar(
            title = { Text(text = "Add Expense") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            },
        )
        Text(text = "NPR 0.00", style = MaterialTheme.typography.displayMedium)
        OutlinedTextField(
            value = "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Merchant or title") },
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = "Transport",
            onValueChange = {},
            readOnly = true,
            label = { Text("Category") },
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = "Cash",
            onValueChange = {},
            readOnly = true,
            label = { Text("Payment method") },
            modifier = Modifier.fillMaxWidth(),
        )
        Button(onClick = {}, enabled = false, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Save expense")
        }
    }
}
