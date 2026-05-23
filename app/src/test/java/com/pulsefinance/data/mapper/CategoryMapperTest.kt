package com.pulsefinance.data.mapper

import com.pulsefinance.data.local.entity.CategoryEntity
import com.pulsefinance.domain.model.Category
import org.junit.Assert.assertEquals
import org.junit.Test

class CategoryMapperTest {

    @Test
    fun `entity to domain maps all fields`() {
        val entity = CategoryEntity(
            id = 1,
            name = "Food & Dining",
            iconKey = "food_dining",
            colorHex = "#FF6B35",
            sortOrder = 1,
            isDefault = true,
            isArchived = false,
        )

        val domain = entity.toDomain()

        assertEquals(1L, domain.id)
        assertEquals("Food & Dining", domain.name)
        assertEquals("food_dining", domain.iconKey)
        assertEquals("#FF6B35", domain.colorHex)
        assertEquals(1, domain.sortOrder)
        assertEquals(true, domain.isDefault)
        assertEquals(false, domain.isArchived)
    }

    @Test
    fun `domain to entity maps all fields`() {
        val domain = Category(
            id = 2,
            name = "Transport",
            iconKey = "transport",
            colorHex = "#4ECDC4",
            sortOrder = 2,
            isDefault = true,
            isArchived = false,
        )

        val entity = domain.toEntity()

        assertEquals(2L, entity.id)
        assertEquals("Transport", entity.name)
        assertEquals("transport", entity.iconKey)
        assertEquals("#4ECDC4", entity.colorHex)
        assertEquals(2, entity.sortOrder)
        assertEquals(true, entity.isDefault)
        assertEquals(false, entity.isArchived)
    }

    @Test
    fun `round trip preserves all data`() {
        val original = CategoryEntity(
            id = 5,
            name = "Groceries",
            iconKey = "groceries",
            colorHex = "#95E1D3",
            sortOrder = 3,
            isDefault = true,
            isArchived = false,
        )

        assertEquals(original, original.toDomain().toEntity())
    }
}
