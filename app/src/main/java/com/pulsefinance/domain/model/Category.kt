package com.pulsefinance.domain.model

data class Category(
    val id: Long,
    val name: String,
    val iconKey: String,
    val colorHex: String,
    val sortOrder: Int,
    val isDefault: Boolean,
    val isArchived: Boolean,
) {
    init {
        require(id > 0) { "Category id must be positive." }
        require(name.isNotBlank()) { "Category name is required." }
        require(colorHex.isNotBlank()) { "Category color is required." }
    }
}
