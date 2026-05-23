package com.pulsefinance.data.local.dao

import androidx.room.ColumnInfo

data class CategorySpendRow(
    @ColumnInfo(name = "category_id")
    val categoryId: Long,
    @ColumnInfo(name = "category_name")
    val categoryName: String,
    @ColumnInfo(name = "color_hex")
    val colorHex: String,
    @ColumnInfo(name = "amount_minor")
    val amountMinor: Long,
    @ColumnInfo(name = "transaction_count")
    val transactionCount: Int,
)
