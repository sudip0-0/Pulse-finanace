package com.pulsefinance.presentation.common.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import com.pulsefinance.presentation.common.theme.PulseColors
import com.pulsefinance.presentation.common.theme.PulseSpacing
import com.pulsefinance.presentation.navigation.PulseRoute

data class PulseNavItem(
    val route: PulseRoute,
    val label: String,
    val icon: ImageVector,
)

val PulseBottomNavItems = listOf(
    PulseNavItem(PulseRoute.Dashboard, "Home", Icons.Default.Home),
    PulseNavItem(PulseRoute.Analytics, "Analytics", Icons.Default.Analytics),
    PulseNavItem(PulseRoute.Transactions, "Transactions", Icons.AutoMirrored.Filled.ReceiptLong),
    PulseNavItem(PulseRoute.Settings, "Settings", Icons.Default.Settings),
)

@Composable
fun PulseBottomBar(
    selectedRoute: String?,
    onRouteSelected: (PulseRoute) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(
        modifier = modifier
            .navigationBarsPadding()
            .padding(horizontal = PulseSpacing.xl, vertical = PulseSpacing.sm)
            .height(PulseSpacing.navHeight)
            .clip(MaterialTheme.shapes.large),
        containerColor = PulseColors.SurfaceHigh,
        tonalElevation = PulseSpacing.xxs,
    ) {
        PulseBottomNavItems.forEach { item ->
            val selected = selectedRoute == item.route.path
            NavigationBarItem(
                selected = selected,
                onClick = { onRouteSelected(item.route) },
                icon = {
                    Icon(imageVector = item.icon, contentDescription = item.label)
                },
                label = { Text(text = item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PulseColors.Primary,
                    selectedTextColor = PulseColors.Primary,
                    unselectedIconColor = PulseColors.TextSecondary,
                    unselectedTextColor = PulseColors.TextSecondary,
                    indicatorColor = PulseColors.SurfacePressed,
                ),
            )
        }
    }
}
