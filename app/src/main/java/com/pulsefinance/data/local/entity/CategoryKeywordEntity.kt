package com.pulsefinance.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "category_keywords",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["category_id"]),
        Index(value = ["keyword", "locale"], unique = true),
    ],
)
data class CategoryKeywordEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "category_id")
    val categoryId: Long,
    @ColumnInfo(name = "keyword")
    val keyword: String,
    @ColumnInfo(name = "match_type")
    val matchType: KeywordMatchType,
    @ColumnInfo(name = "weight")
    val weight: Int,
    @ColumnInfo(name = "locale")
    val locale: String,
)
