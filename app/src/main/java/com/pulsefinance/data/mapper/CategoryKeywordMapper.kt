package com.pulsefinance.data.mapper

import com.pulsefinance.data.local.entity.CategoryKeywordEntity
import com.pulsefinance.domain.model.CategoryKeyword
import com.pulsefinance.domain.model.KeywordMatchType
import com.pulsefinance.data.local.entity.KeywordMatchType as EntityMatchType

fun CategoryKeywordEntity.toDomain(): CategoryKeyword = CategoryKeyword(
    id = id,
    categoryId = categoryId,
    keyword = keyword,
    matchType = matchType.toDomain(),
    weight = weight,
    locale = locale,
)

fun CategoryKeyword.toEntity(): CategoryKeywordEntity = CategoryKeywordEntity(
    id = id,
    categoryId = categoryId,
    keyword = keyword,
    matchType = matchType.toEntity(),
    weight = weight,
    locale = locale,
)

private fun EntityMatchType.toDomain(): KeywordMatchType = when (this) {
    EntityMatchType.Merchant -> KeywordMatchType.Merchant
    EntityMatchType.Keyword -> KeywordMatchType.Keyword
}

private fun KeywordMatchType.toEntity(): EntityMatchType = when (this) {
    KeywordMatchType.Merchant -> EntityMatchType.Merchant
    KeywordMatchType.Keyword -> EntityMatchType.Keyword
}
