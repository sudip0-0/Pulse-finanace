package com.pulsefinance.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.LocalDate

@Entity(
    tableName = "recurring_rules",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [
        Index(value = ["category_id"]),
        Index(value = ["next_due_date"]),
        Index(value = ["is_active"]),
    ],
)
data class RecurringRuleEntity(
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
    @ColumnInfo(name = "frequency")
    val frequency: RecurringFrequency,
    @ColumnInfo(name = "interval")
    val interval: Int,
    @ColumnInfo(name = "start_date")
    val startDate: LocalDate,
    @ColumnInfo(name = "next_due_date")
    val nextDueDate: LocalDate,
    @ColumnInfo(name = "end_date")
    val endDate: LocalDate?,
    @ColumnInfo(name = "is_active")
    val isActive: Boolean,
    @ColumnInfo(name = "created_at")
    val createdAt: Instant,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Instant,
)
