package com.pulsefinance.domain.receipt

import com.pulsefinance.domain.model.PaymentMethod
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ReceiptTextParserTest {
    private val today = LocalDate.of(2026, 5, 30)

    @Test
    fun parsesPathaoStyleReceipt() {
        val text = """
            Pathao Nepal
            Trip ID: 12345
            Date: 23/05/2026
            Fare
            Rs 280.00
            TOTAL Rs 280.00
            Thank you
        """.trimIndent()

        val draft = ReceiptTextParser.parse(text, today)

        assertEquals("Pathao Nepal", draft.merchant)
        assertEquals(28000L, draft.amountMinor)
        assertEquals(LocalDate.of(2026, 5, 23), draft.expenseDate)
    }

    @Test
    fun parsesBhatBhateniTotalWithCommas() {
        val text = """
            Bhat-Bhateni Supermarket
            01/05/2026
            Groceries
            SUBTOTAL Rs 1,234.00
            VAT
            GRAND TOTAL Rs 1,418.50
        """.trimIndent()

        val draft = ReceiptTextParser.parse(text, today)

        assertEquals("Bhat-Bhateni Supermarket", draft.merchant)
        assertEquals(141850L, draft.amountMinor)
    }

    @Test
    fun parsesEsewaPaymentSlip() {
        val text = """
            eSewa Payment Successful
            Merchant: Foodmandu
            Date: 2026-05-22
            Amount NPR 450.50
            Transaction ID 998877
        """.trimIndent()

        val draft = ReceiptTextParser.parse(text, today)

        assertEquals("Foodmandu", draft.merchant)
        assertEquals(45050L, draft.amountMinor)
        assertEquals(PaymentMethod.Esewa, draft.paymentMethodHint)
        assertEquals(LocalDate.of(2026, 5, 22), draft.expenseDate)
    }

    @Test
    fun parsesNeaUtilityBill() {
        val text = """
            Nepal Electricity Authority
            Consumer: John Doe
            Bill Date 15-04-2026
            Energy Charge Rs 850.00
            TOTAL AMOUNT Rs 1,250.00
        """.trimIndent()

        val draft = ReceiptTextParser.parse(text, today)

        assertEquals("Nepal Electricity Authority", draft.merchant)
        assertEquals(125000L, draft.amountMinor)
    }

    @Test
    fun parsesFuzzyOcrNoise() {
        val text = """
            DARAZ NEPAL
            lnvoice #A123
            20/05/26
            ltems 2,199.00
            NET TOTAL Rs 2,199.00
        """.trimIndent()

        val draft = ReceiptTextParser.parse(text, today)

        assertEquals("DARAZ NEPAL", draft.merchant)
        assertEquals(219900L, draft.amountMinor)
    }

    @Test
    fun parsesKhaltiReceipt() {
        val text = """
            Khalti
            Paid to WorldLink
            30/05/2026
            Amount: Rs 1,500.00
        """.trimIndent()

        val draft = ReceiptTextParser.parse(text, today)

        assertNotNull(draft.amountMinor)
        assertEquals(150000L, draft.amountMinor)
        assertEquals(PaymentMethod.Khalti, draft.paymentMethodHint)
    }

    @Test
    fun returnsNullAmountWhenNoNumbers() {
        val draft = ReceiptTextParser.parse("Thank you for visiting", today)
        assertNull(draft.amountMinor)
    }
}
