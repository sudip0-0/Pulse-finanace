package com.pulsefinance.data.local.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pulsefinance.data.local.entity.BudgetEntity
import com.pulsefinance.data.local.entity.ExpenseEntity
import com.pulsefinance.data.local.entity.RecurringFrequency
import com.pulsefinance.data.local.entity.RecurringRuleEntity
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PulseDatabaseInstrumentedTest {
    private lateinit var database: PulseDatabase

    @Before
    fun createDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, PulseDatabase::class.java)
            .addCallback(
                object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        PulseSeedData.seed(db)
                    }
                },
            )
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun seedDataIncludesNepalCategoriesAndKeywords() = runBlocking {
        val categories = database.categoryDao().observeActiveCategories().first()
        val pathao = database.categoryKeywordDao().getKeyword("pathao")
        val worldLink = database.categoryKeywordDao().getKeyword("worldlink")

        assertEquals("Food & Dining", categories.first().name)
        assertNotNull(pathao)
        assertNotNull(worldLink)
    }

    @Test
    fun keywordLookupMatchesCaseInsensitively() = runBlocking {
        val matches = database.categoryKeywordDao()
            .findKeywordMatches("PATHAO RIDE TO BANESHWOR")

        assertEquals("pathao", matches.first().keyword)
        assertEquals(PulseSeedData.CategoryId.TRANSPORT, matches.first().categoryId)
    }

    @Test
    fun expenseDateRangeAndCategoryAggregationUseMinorUnits() = runBlocking {
        val now = Instant.parse("2026-05-23T10:15:30Z")
        database.expenseDao().insert(
            ExpenseEntity(
                title = "Pathao ride to Baneshwor",
                merchant = "Pathao",
                amountMinor = 28000,
                currencyCode = "NPR",
                categoryId = PulseSeedData.CategoryId.TRANSPORT,
                paymentMethod = "Cash",
                expenseDate = LocalDate.of(2026, 5, 23),
                note = null,
                isRecurringGenerated = false,
                recurringRuleId = null,
                createdAt = now,
                updatedAt = now,
            ),
        )

        val expenses = database.expenseDao()
            .observeExpensesBetween(LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 31))
            .first()
        val categorySpend = database.expenseDao()
            .observeCategorySpendingBetween(LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 31))
            .first()

        assertEquals(1, expenses.size)
        assertEquals(28000, expenses.first().amountMinor)
        assertEquals("Transport", categorySpend.first().categoryName)
        assertEquals(28000, categorySpend.first().amountMinor)
    }

    @Test
    fun duplicateBudgetMonthInsertAbortsInsteadOfReplacing() = runBlocking {
        val now = Instant.parse("2026-05-23T10:15:30Z")
        val month = YearMonth.of(2026, 5)
        database.budgetDao().insert(
            BudgetEntity(
                month = month,
                amountMinor = 350000,
                currencyCode = "NPR",
                createdAt = now,
                updatedAt = now,
            ),
        )

        try {
            database.budgetDao().insert(
                BudgetEntity(
                    month = month,
                    amountMinor = 400000,
                    currencyCode = "NPR",
                    createdAt = now,
                    updatedAt = now,
                ),
            )
            fail("Expected duplicate month insert to fail")
        } catch (_: Exception) {
            val saved = database.budgetDao().getBudgetForMonth(month)
            assertEquals(350000, saved?.amountMinor)
        }
    }

    @Test
    fun recurringDueQueryReturnsActiveRulesDueOnOrBeforeDate() = runBlocking {
        val now = Instant.parse("2026-05-23T10:15:30Z")
        database.recurringRuleDao().insert(
            RecurringRuleEntity(
                title = "WorldLink internet",
                merchant = "WorldLink",
                amountMinor = 150000,
                currencyCode = "NPR",
                categoryId = PulseSeedData.CategoryId.INTERNET_TV,
                frequency = RecurringFrequency.Monthly,
                interval = 1,
                startDate = LocalDate.of(2026, 5, 1),
                nextDueDate = LocalDate.of(2026, 5, 20),
                endDate = null,
                isActive = true,
                createdAt = now,
                updatedAt = now,
            ),
        )

        val dueRules = database.recurringRuleDao()
            .getActiveRulesDueOnOrBefore(LocalDate.of(2026, 5, 23))

        assertEquals(1, dueRules.size)
        assertEquals(RecurringFrequency.Monthly, dueRules.first().frequency)
    }
}
