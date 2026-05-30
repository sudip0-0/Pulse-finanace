package com.pulsefinance.domain.receipt

import com.pulsefinance.domain.model.Money
import com.pulsefinance.domain.model.PaymentMethod
import java.time.LocalDate

object ReceiptTextParser {
    private val TOTAL_KEYWORDS = listOf(
        "GRAND TOTAL",
        "NET TOTAL",
        "TOTAL AMOUNT",
        "AMOUNT DUE",
        "NET AMOUNT",
        "TOTAL",
        "SUB TOTAL",
        "SUBTOTAL",
    )

    private val SKIP_AMOUNT_LINE_PATTERNS = listOf(
        Regex("""(?i)transaction\s*id"""),
        Regex("""(?i)invoice\s*#?"""),
        Regex("""(?i)trip\s*id"""),
        Regex("""(?i)consumer\s*no"""),
    )

    private val SKIP_MERCHANT_PATTERNS = listOf(
        Regex("""(?i)^\s*(vat|pan|tax|bill\s*no|invoice\s*no|tel|phone|mobile|www\.|http)"""),
        Regex("""(?i)^\s*\d{5,}\s*$"""),
        Regex("""(?i)^\s*(thank you|receipt|copy|customer\s*copy)"""),
    )

    private val DATE_PATTERNS = listOf(
        Regex("""(\d{4})[/.-](\d{1,2})[/.-](\d{1,2})"""),
        Regex("""(\d{1,2})[/.-](\d{1,2})[/.-](\d{2,4})"""),
    )

    private val PAYMENT_KEYWORDS = listOf(
        "connectips" to PaymentMethod.Bank,
        "fonepay" to PaymentMethod.Fonepay,
        "esewa" to PaymentMethod.Esewa,
        "e-sewa" to PaymentMethod.Esewa,
        "khalti" to PaymentMethod.Khalti,
        "ime pay" to PaymentMethod.Bank,
        "card" to PaymentMethod.Card,
        "visa" to PaymentMethod.Card,
        "mastercard" to PaymentMethod.Card,
        "bank" to PaymentMethod.Bank,
        "cash" to PaymentMethod.Cash,
    )

    fun parse(rawText: String, today: LocalDate = LocalDate.now()): ReceiptDraft {
        val lines = rawText.lines()
            .map { it.trim() }
            .filter { it.isNotBlank() }

        val normalizedText = lines.joinToString("\n")
        val amountMinor = extractAmount(lines, normalizedText)
        val merchant = extractMerchant(lines)
        val expenseDate = extractDate(normalizedText) ?: today
        val paymentMethod = extractPaymentMethod(normalizedText)
        val title = merchant
        val note = buildNote(amountMinor, lines)

        return ReceiptDraft(
            amountMinor = amountMinor,
            merchant = merchant,
            title = title,
            expenseDate = expenseDate,
            paymentMethodHint = paymentMethod,
            note = note,
            rawText = normalizedText,
        )
    }

    private fun extractAmount(lines: List<String>, fullText: String): Long? {
        val candidates = mutableListOf<Pair<Long, Int>>()

        lines.forEachIndexed { index, line ->
            if (SKIP_AMOUNT_LINE_PATTERNS.any { it.containsMatchIn(line) }) return@forEachIndexed
            if (looksLikeDate(line)) return@forEachIndexed
            val upper = line.uppercase()
            val totalScore = totalKeywordScore(upper)
            extractAmountsFromLine(line).forEach { minor ->
                var score = 1
                if (totalScore > 0) score += totalScore
                if (index >= lines.size - 5) score += 3
                if (upper.contains("RS") || upper.contains("NPR") || line.contains("रू")) score += 2
                candidates += minor to score
            }
        }

        if (candidates.isEmpty()) {
            extractAmountsFromLine(fullText).maxOrNull()?.let { return it }
            return null
        }

        return candidates.maxWithOrNull(compareBy<Pair<Long, Int>> { it.second })?.first
    }

    private fun extractAmountsFromLine(line: String): List<Long> {
        val pattern = Regex("""(?:Rs\.?|NPR|रू)?\s*([\d,]+(?:\.\d{1,2})?)""", RegexOption.IGNORE_CASE)
        return pattern.findAll(line).mapNotNull { match ->
            parseAmountMinor(match.groupValues[1])
        }.toList()
    }

    private fun parseAmountMinor(text: String): Long? {
        val cleaned = text.replace(",", "").trim()
        if (cleaned.isBlank()) return null
        val parts = cleaned.split(".")
        val major = parts[0].toLongOrNull() ?: return null
        if (major > 99_999_999) return null
        val minor = when {
            parts.size == 1 -> 0L
            parts[1].isEmpty() -> 0L
            parts[1].length == 1 -> parts[1].toLongOrNull()?.times(10) ?: return null
            else -> parts[1].take(2).toLongOrNull() ?: return null
        }
        val total = major * Money.MINOR_UNITS_PER_MAJOR + minor
        return total.takeIf { it > 0 }
    }

    private fun totalKeywordScore(upper: String): Int {
        return when {
            upper.contains("GRAND TOTAL") -> 20
            upper.contains("NET TOTAL") -> 18
            upper.contains("TOTAL AMOUNT") || upper.contains("AMOUNT DUE") -> 16
            upper.contains("NET AMOUNT") -> 14
            upper.contains("SUBTOTAL") || upper.contains("SUB TOTAL") -> 5
            TOTAL_KEYWORDS.any { upper.contains(it) } -> 10
            else -> 0
        }
    }

    private fun extractMerchant(lines: List<String>): String? {
        val labeled = Regex("""(?i)merchant\s*:\s*(.+)""").find(lines.joinToString("\n"))?.groupValues?.get(1)?.trim()
        if (!labeled.isNullOrBlank()) return labeled.take(80)

        return lines.firstOrNull { line ->
            line.length in 3..60 &&
                !looksLikeDate(line) &&
                extractAmountsFromLine(line).isEmpty() &&
                SKIP_MERCHANT_PATTERNS.none { it.containsMatchIn(line) }
        }?.take(80)
    }

    private fun extractDate(text: String): LocalDate? {
        for (pattern in DATE_PATTERNS) {
            val match = pattern.find(text) ?: continue
            val groups = match.groupValues.drop(1)
            val parsed = when (groups.size) {
                3 -> if (groups[0].length == 4) {
                    parseYmd(groups[0].toInt(), groups[1].toInt(), groups[2].toInt())
                } else {
                    parseDmy(groups[0].toInt(), groups[1].toInt(), groups[2].toInt())
                }
                else -> null
            }
            if (parsed != null) return parsed
        }
        return null
    }

    private fun parseDmy(day: Int, month: Int, year: Int): LocalDate? {
        val fullYear = if (year < 100) 2000 + year else year
        return runCatching { LocalDate.of(fullYear, month, day) }.getOrNull()
    }

    private fun parseYmd(year: Int, month: Int, day: Int): LocalDate? {
        return runCatching { LocalDate.of(year, month, day) }.getOrNull()
    }

    private fun looksLikeDate(line: String): Boolean {
        return DATE_PATTERNS.any { it.containsMatchIn(line) }
    }

    private fun extractPaymentMethod(text: String): PaymentMethod? {
        val lower = text.lowercase()
        return PAYMENT_KEYWORDS.firstOrNull { (keyword, _) -> lower.contains(keyword) }?.second
    }

    private fun buildNote(amountMinor: Long?, lines: List<String>): String? {
        val totalLine = lines.firstOrNull { line ->
            TOTAL_KEYWORDS.any { line.uppercase().contains(it) }
        }
        val parts = buildList {
            add("Scanned receipt")
            if (totalLine != null) add(totalLine)
        }
        return parts.joinToString(" · ").takeIf { amountMinor != null || totalLine != null }
    }
}
