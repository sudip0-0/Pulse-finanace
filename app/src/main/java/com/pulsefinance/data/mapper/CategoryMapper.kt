package com.pulsefinance.data.mapper

import com.pulsefinance.data.local.entity.CategoryEntity
import com.pulsefinance.domain.model.Category

fun CategoryEntity.toDomain(): Category = Category(
    id = id,
    name = name,
    iconKey = iconKey,
    colorHex = colorHex,
    sortOrder = sortOrder,
    isDefault = isDefault,
    isArchived = isArchived,
)

fun Category.toEntity(): CategoryEntity = CategoryEntity(
    id = id,
    name = name,
    iconKey = iconKey,
    colorHex = colorHex,
    sortOrder = sortOrder,
    isDefault = isDefault,
    isArchived = isArchived,
)
