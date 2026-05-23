package com.pulsefinance.domain.categorization

object TextNormalizer {
    private val punctuation = Regex("[^\\p{L}\\p{N}\\s]+")
    private val whitespace = Regex("\\s+")

    fun normalize(vararg values: String?): String {
        return values
            .filterNotNull()
            .joinToString(" ")
            .lowercase()
            .replace(punctuation, " ")
            .replace(whitespace, " ")
            .trim()
    }
}
