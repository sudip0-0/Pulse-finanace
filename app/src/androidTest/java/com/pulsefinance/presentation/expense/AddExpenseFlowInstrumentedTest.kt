package com.pulsefinance.presentation.expense

import android.content.Context
import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pulsefinance.HiltTestActivity
import com.pulsefinance.data.local.database.PulseDatabase
import com.pulsefinance.presentation.common.theme.PulseTheme
import com.pulsefinance.presentation.navigation.PulseNavGraph
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddExpenseFlowInstrumentedTest {

    @get:Rule
    val composeRule = createEmptyComposeRule()

    private var scenario: ActivityScenario<HiltTestActivity>? = null

    @Before
    fun resetDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.deleteDatabase(PulseDatabase.DATABASE_NAME)
    }

    @After
    fun closeScenario() {
        scenario?.close()
    }

    @Test
    fun addExpenseUpdatesDashboardFromPersistedData() {
        scenario = ActivityScenario.launch(HiltTestActivity::class.java).also { launchedScenario ->
            launchedScenario.onActivity { activity ->
                activity.setContent {
                    PulseTheme {
                        PulseNavGraph()
                    }
                }
            }
        }

        composeRule.onNodeWithText("Add expense").performClick()
        composeRule.onNodeWithText("Amount").performTextInput("280")
        composeRule.onNodeWithText("Title").performTextInput("Pathao ride")
        composeRule.onNodeWithText("Merchant (optional)").performTextInput("Pathao")

        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText("Transport").fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithText("Save expense").performClick()

        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText("This month").fetchSemanticsNodes().isNotEmpty() &&
                composeRule.onAllNodesWithText("Pathao").fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithContentDescription("Monthly spend: रू 280.00").assertIsDisplayed()
        composeRule.onNodeWithText("Pathao").assertIsDisplayed()
    }
}
