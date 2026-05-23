package com.pulsefinance.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.pulsefinance.presentation.common.theme.PulseColors
import com.pulsefinance.presentation.common.theme.PulseSpacing

@Composable
fun PulseCard(
    modifier: Modifier = Modifier,
    containerColor: Color = PulseColors.Surface,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        content = {
            androidx.compose.foundation.layout.Column(
                modifier = Modifier
                    .background(containerColor)
                    .padding(PulseSpacing.lg),
                content = content,
            )
        },
    )
}
