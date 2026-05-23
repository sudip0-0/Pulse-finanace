package com.pulsefinance.presentation.common.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val PulseColorScheme: ColorScheme = darkColorScheme(
    primary = PulseColors.Primary,
    background = PulseColors.Background,
    surface = PulseColors.Surface,
    surfaceVariant = PulseColors.SurfaceHigh,
    onPrimary = PulseColors.TextPrimary,
    onBackground = PulseColors.TextPrimary,
    onSurface = PulseColors.TextPrimary,
    onSurfaceVariant = PulseColors.TextSecondary,
    error = PulseColors.Danger,
)

@Composable
fun PulseTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PulseColorScheme,
        typography = PulseTypography,
        shapes = PulseShapes,
        content = content,
    )
}
