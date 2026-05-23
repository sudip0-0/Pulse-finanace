package com.pulsefinance.presentation.preview

import androidx.compose.ui.graphics.Color
import com.pulsefinance.presentation.common.theme.PulseColors

data class CategoryPreview(
    val name: String,
    val amount: String,
    val percent: String,
    val color: Color,
)

data class TransactionPreview(
    val merchant: String,
    val category: String,
    val amount: String,
    val dateLabel: String,
    val color: Color,
)

object PulsePreviewData {
    val categories = listOf(
        CategoryPreview("Food & Dining", "NPR 682.40", "28%", PulseColors.Food),
        CategoryPreview("Transport", "NPR 375.20", "16%", PulseColors.Transport),
        CategoryPreview("Utilities", "NPR 575.30", "24%", PulseColors.Utilities),
        CategoryPreview("Shopping", "NPR 420.00", "17%", PulseColors.Shopping),
    )

    val transactions = listOf(
        TransactionPreview("Pathao", "Transport", "-NPR 280.00", "Today", PulseColors.Transport),
        TransactionPreview("Foodmandu", "Food & Dining", "-NPR 890.00", "Yesterday", PulseColors.Food),
        TransactionPreview("Daraz", "Shopping", "-NPR 1,250.00", "May 21", PulseColors.Shopping),
        TransactionPreview("Bhat-Bhateni", "Groceries", "-NPR 2,450.00", "May 20", PulseColors.Groceries),
        TransactionPreview("eSewa", "Wallet & Transfers", "-NPR 500.00", "May 19", PulseColors.WalletTransfers),
        TransactionPreview("Khalti", "Wallet & Transfers", "-NPR 300.00", "May 18", PulseColors.WalletTransfers),
        TransactionPreview("NTC", "Mobile Recharge", "-NPR 199.00", "May 17", PulseColors.MobileRecharge),
        TransactionPreview("Ncell", "Mobile Recharge", "-NPR 149.00", "May 16", PulseColors.MobileRecharge),
        TransactionPreview("NEA", "Utilities", "-NPR 1,120.00", "May 15", PulseColors.Utilities),
        TransactionPreview("WorldLink", "Internet & TV", "-NPR 1,500.00", "May 14", PulseColors.InternetTv),
        TransactionPreview("Vianet", "Internet & TV", "-NPR 1,400.00", "May 13", PulseColors.InternetTv),
    )

    val quickAddMerchants = listOf("Other", "Food", "Pathao", "Daraz", "Fuel", "NTC/Ncell", "eSewa/Khalti")
}
