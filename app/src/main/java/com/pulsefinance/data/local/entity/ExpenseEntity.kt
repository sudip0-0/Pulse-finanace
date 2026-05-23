package com.pulsefinance.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.LocalDate

@Entity(
    tableName = "expenses",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.RESTRICT,
        ),
        ForeignKey(
            entity = RecurringRuleEntity::class,
            parentColumns = ["id"],
            childColumns = ["recurring_rule_id"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [
        Index(value = ["category_id"]),
        Index(value = ["expense_date"]),
        Index(value = ["expense_date", "created_at"]),
        Index(value = ["expense_date", "category_id"]),
        Index(value = ["merchant"]),
        Index(value = ["recurring_rule_id"]),
    ],
)
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "merchant")
    val merchant: String?,
    @ColumnInfo(name = "amount_minor")
    val amountMinor: Long,
    @ColumnInfo(name = "currency_code")
    val currencyCode: String,
    @ColumnInfo(name = "category_id")
    val categoryId: Long,
    @ColumnInfo(name = "payment_method")
    val paymentMethod: String?,
    @ColumnInfo(name = "expense_date")
    val expenseDate: LocalDate,
    @ColumnInfo(name = "note")
    val note: String?,
    @ColumnInfo(name = "is_recurring_generated")
    val isRecurringGenerated: Boolean,
    @ColumnInfo(name = "recurring_rule_id")
    val recurringRuleId: Long?,
    @ColumnInfo(name = "created_at")
    val createdAt: Instant,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Instant,
)
