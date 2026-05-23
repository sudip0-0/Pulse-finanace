package com.pulsefinance.data.mapper

import com.pulsefinance.data.local.entity.CategoryKeywordEntity
import com.pulsefinance.domain.model.CategoryKeyword
import com.pulsefinance.domain.model.KeywordMatchType
import org.junit.Assert.assertEquals
import org.junit.Test
import com.pulsefinance.data.local.entity.KeywordMatchType as EntityMatchType

class CategoryKeywordMapperTest {

    @Test
    fun `entity to domain maps merchant match type`() {
        val entity = CategoryKeywordEntity(
            id = 1,
            categoryId = 3,
            keyword = "pathao",
            matchType = EntityMatchType.Merchant,
            weight = 100,
            locale = "en-NP",
        )

        val domain = entity.toDomain()

        assertEquals(1L, domain.id)
        assertEquals(3L, domain.categoryId)
        assertEquals("pathao", domain.keyword)
        assertEquals(KeywordMatchType.Merchant, domain.matchType)
        assertEquals(100, domain.weight)
        assertEquals("en-NP", domain.locale)
    }

    @Test
    fun `entity to domain maps keyword match type`() {
        val entity = CategoryKeywordEntity(
            id = 2,
            categoryId = 1,
            keyword = "momo",
            matchType = EntityMatchType.Keyword,
            weight = 60,
            locale = "en-NP",
        )

        val domain = entity.toDomain()

        assertEquals(KeywordMatchType.Keyword, domain.matchType)
    }

    @Test
    fun `domain to entity maps match types correctly`() {
        val merchantKeyword = CategoryKeyword(
            id = 3,
            categoryId = 4,
            keyword = "daraz",
            matchType = KeywordMatchType.Merchant,
            weight = 100,
            locale = "en-NP",
        )

        val keywordKeyword = CategoryKeyword(
            id = 4,
            categoryId = 1,
            keyword = "lunch",
            matchType = KeywordMatchType.Keyword,
            weight = 50,
            locale = "en-NP",
        )

        assertEquals(EntityMatchType.Merchant, merchantKeyword.toEntity().matchType)
        assertEquals(EntityMatchType.Keyword, keywordKeyword.toEntity().matchType)
    }

    @Test
    fun `round trip preserves all data`() {
        val original = CategoryKeywordEntity(
            id = 10,
            categoryId = 6,
            keyword = "ntc",
            matchType = EntityMatchType.Merchant,
            weight = 100,
            locale = "en-NP",
        )

        assertEquals(original, original.toDomain().toEntity())
    }
}
