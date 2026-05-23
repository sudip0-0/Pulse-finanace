package com.pulsefinance.data.local.database

import androidx.sqlite.db.SupportSQLiteDatabase
import com.pulsefinance.data.local.entity.KeywordMatchType

object PulseSeedData {
    object CategoryId {
        const val FOOD_DINING = 1L
        const val GROCERIES = 2L
        const val TRANSPORT = 3L
        const val SHOPPING = 4L
        const val WALLET_TRANSFERS = 5L
        const val MOBILE_RECHARGE = 6L
        const val UTILITIES = 7L
        const val RENT_HOUSING = 8L
        const val INTERNET_TV = 9L
        const val EDUCATION = 10L
        const val HEALTH = 11L
        const val ENTERTAINMENT = 12L
        const val FUEL = 13L
        const val TRAVEL = 14L
        const val SAVINGS = 15L
        const val OTHER = 16L
    }

    val statements: List<String> = categoryStatements() + keywordStatements()

    fun seed(db: SupportSQLiteDatabase) {
        statements.forEach(db::execSQL)
    }

    private fun categoryStatements(): List<String> = listOf(
        categorySql(CategoryId.FOOD_DINING, "Food & Dining", "restaurant", "#35C76B", 10),
        categorySql(CategoryId.GROCERIES, "Groceries", "shopping_basket", "#2ECC71", 20),
        categorySql(CategoryId.TRANSPORT, "Transport", "directions_car", "#2F80FF", 30),
        categorySql(CategoryId.SHOPPING, "Shopping", "shopping_bag", "#E94F86", 40),
        categorySql(CategoryId.WALLET_TRANSFERS, "Wallet & Transfers", "account_balance_wallet", "#2DD4BF", 50),
        categorySql(CategoryId.MOBILE_RECHARGE, "Mobile Recharge", "smartphone", "#38BDF8", 60),
        categorySql(CategoryId.UTILITIES, "Utilities", "bolt", "#FFB020", 70),
        categorySql(CategoryId.RENT_HOUSING, "Rent & Housing", "home", "#94A3B8", 80),
        categorySql(CategoryId.INTERNET_TV, "Internet & TV", "wifi", "#7C5CFF", 90),
        categorySql(CategoryId.EDUCATION, "Education", "school", "#A78BFA", 100),
        categorySql(CategoryId.HEALTH, "Health", "medical_services", "#F87171", 110),
        categorySql(CategoryId.ENTERTAINMENT, "Entertainment", "movie", "#F472B6", 120),
        categorySql(CategoryId.FUEL, "Fuel", "local_gas_station", "#F59E0B", 130),
        categorySql(CategoryId.TRAVEL, "Travel", "flight", "#22C55E", 140),
        categorySql(CategoryId.SAVINGS, "Savings", "savings", "#60A5FA", 150),
        categorySql(CategoryId.OTHER, "Other", "category", "#9CA3AF", 160),
    )

    private fun keywordStatements(): List<String> {
        var id = 1L
        fun keyword(
            categoryId: Long,
            value: String,
            matchType: KeywordMatchType = KeywordMatchType.Keyword,
            weight: Int = 50,
        ): KeywordSeed {
            return KeywordSeed(id++, categoryId, value, matchType, weight)
        }

        return listOf(
            keyword(CategoryId.TRANSPORT, "pathao", KeywordMatchType.Merchant, 100),
            keyword(CategoryId.TRANSPORT, "tootle", KeywordMatchType.Merchant, 100),
            keyword(CategoryId.TRANSPORT, "indrive", KeywordMatchType.Merchant, 100),
            keyword(CategoryId.TRANSPORT, "in drive", KeywordMatchType.Merchant, 95),
            keyword(CategoryId.FOOD_DINING, "foodmandu", KeywordMatchType.Merchant, 100),
            keyword(CategoryId.FOOD_DINING, "bhojdeals", KeywordMatchType.Merchant, 100),
            keyword(CategoryId.FOOD_DINING, "bhoj deals", KeywordMatchType.Merchant, 95),
            keyword(CategoryId.FOOD_DINING, "momo", weight = 70),
            keyword(CategoryId.FOOD_DINING, "khaja", weight = 70),
            keyword(CategoryId.GROCERIES, "bhat-bhateni", KeywordMatchType.Merchant, 100),
            keyword(CategoryId.GROCERIES, "bhatbhateni", KeywordMatchType.Merchant, 100),
            keyword(CategoryId.GROCERIES, "big mart", KeywordMatchType.Merchant, 100),
            keyword(CategoryId.GROCERIES, "salesberry", KeywordMatchType.Merchant, 100),
            keyword(CategoryId.GROCERIES, "kirana", weight = 75),
            keyword(CategoryId.SHOPPING, "daraz", KeywordMatchType.Merchant, 100),
            keyword(CategoryId.SHOPPING, "sastodeal", KeywordMatchType.Merchant, 100),
            keyword(CategoryId.SHOPPING, "sasto deal", KeywordMatchType.Merchant, 95),
            keyword(CategoryId.WALLET_TRANSFERS, "esewa", KeywordMatchType.Merchant, 100),
            keyword(CategoryId.WALLET_TRANSFERS, "khalti", KeywordMatchType.Merchant, 100),
            keyword(CategoryId.WALLET_TRANSFERS, "ime pay", KeywordMatchType.Merchant, 100),
            keyword(CategoryId.WALLET_TRANSFERS, "connectips", KeywordMatchType.Merchant, 100),
            keyword(CategoryId.WALLET_TRANSFERS, "connect ips", KeywordMatchType.Merchant, 95),
            keyword(CategoryId.WALLET_TRANSFERS, "fonepay", KeywordMatchType.Merchant, 100),
            keyword(CategoryId.MOBILE_RECHARGE, "ntc", KeywordMatchType.Merchant, 100),
            keyword(CategoryId.MOBILE_RECHARGE, "ncell", KeywordMatchType.Merchant, 100),
            keyword(CategoryId.MOBILE_RECHARGE, "recharge", weight = 80),
            keyword(CategoryId.MOBILE_RECHARGE, "data pack", weight = 80),
            keyword(CategoryId.UTILITIES, "nea", KeywordMatchType.Merchant, 100),
            keyword(CategoryId.UTILITIES, "electricity", weight = 85),
            keyword(CategoryId.UTILITIES, "khanepani", KeywordMatchType.Merchant, 100),
            keyword(CategoryId.INTERNET_TV, "worldlink", KeywordMatchType.Merchant, 100),
            keyword(CategoryId.INTERNET_TV, "vianet", KeywordMatchType.Merchant, 100),
            keyword(CategoryId.INTERNET_TV, "classic tech", KeywordMatchType.Merchant, 100),
            keyword(CategoryId.INTERNET_TV, "dishhome", KeywordMatchType.Merchant, 100),
            keyword(CategoryId.INTERNET_TV, "cg net", KeywordMatchType.Merchant, 100),
            keyword(CategoryId.INTERNET_TV, "subisu", KeywordMatchType.Merchant, 100),
            keyword(CategoryId.RENT_HOUSING, "rent", weight = 80),
            keyword(CategoryId.RENT_HOUSING, "room rent", weight = 95),
            keyword(CategoryId.RENT_HOUSING, "flat rent", weight = 95),
            keyword(CategoryId.EDUCATION, "school fee", weight = 95),
            keyword(CategoryId.EDUCATION, "school fees", weight = 95),
            keyword(CategoryId.EDUCATION, "college fee", weight = 95),
            keyword(CategoryId.EDUCATION, "tuition", weight = 85),
            keyword(CategoryId.FUEL, "fuel", weight = 80),
            keyword(CategoryId.FUEL, "petrol", weight = 85),
            keyword(CategoryId.FUEL, "diesel", weight = 85),
            keyword(CategoryId.FUEL, "nepal oil", KeywordMatchType.Merchant, 100),
            keyword(CategoryId.HEALTH, "hospital", weight = 85),
            keyword(CategoryId.HEALTH, "clinic", weight = 85),
            keyword(CategoryId.HEALTH, "pharmacy", weight = 85),
            keyword(CategoryId.HEALTH, "medicine", weight = 80),
        ).map(::keywordSql)
    }

    private fun categorySql(
        id: Long,
        name: String,
        iconKey: String,
        colorHex: String,
        sortOrder: Int,
    ): String {
        return """
            INSERT OR IGNORE INTO categories
            (id, name, icon_key, color_hex, sort_order, is_default, is_archived)
            VALUES ($id, '${name.sqlEscaped()}', '${iconKey.sqlEscaped()}', '$colorHex', $sortOrder, 1, 0)
        """.trimIndent()
    }

    private fun keywordSql(seed: KeywordSeed): String {
        return """
            INSERT OR IGNORE INTO category_keywords
            (id, category_id, keyword, match_type, weight, locale)
            VALUES (${seed.id}, ${seed.categoryId}, '${seed.keyword.sqlEscaped()}', '${seed.matchType.storageValue}', ${seed.weight}, 'en-NP')
        """.trimIndent()
    }

    private fun String.sqlEscaped(): String = replace("'", "''")

    private data class KeywordSeed(
        val id: Long,
        val categoryId: Long,
        val keyword: String,
        val matchType: KeywordMatchType,
        val weight: Int,
    )
}
