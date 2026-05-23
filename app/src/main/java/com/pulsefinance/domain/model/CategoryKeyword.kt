package com.pulsefinance.domain.model

data class CategoryKeyword(
    val id: Long,
    val categoryId: Long,
    val keyword: String,
    val matchType: KeywordMatchType,
    val weight: Int,
    val locale: String = "en-NP",
) {
    init {
        require(id > 0) { "Keyword id must be positive." }
        require(categoryId > 0) { "Keyword category id must be positive." }
        require(keyword.isNotBlank()) { "Keyword is required." }
        require(weight >= 0) { "Keyword weight cannot be negative." }
        require(locale.isNotBlank()) { "Keyword locale is required." }
    }
}

enum class KeywordMatchType {
    Merchant,
    Keyword,
}
