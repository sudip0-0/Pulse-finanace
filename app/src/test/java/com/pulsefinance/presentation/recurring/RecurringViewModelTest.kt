package com.pulsefinance.presentation.recurring

import com.pulsefinance.domain.FakeCategoryRepository
import com.pulsefinance.domain.FakeRecurringRuleRepository
import com.pulsefinance.domain.category
import com.pulsefinance.domain.recurringRule
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RecurringViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadsRulesOnInit() = runTest {
        val rule = recurringRule()
        val recurringRepo = FakeRecurringRuleRepository(listOf(rule))
        val categoryRepo = FakeCategoryRepository(listOf(category(9, "Internet & TV")))
        val viewModel = RecurringViewModel(recurringRepo, categoryRepo)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(1, state.rules.size)
        assertEquals("WorldLink internet", state.rules[0].title)
        assertEquals("Internet & TV", state.rules[0].categoryName)
        assertTrue(state.rules[0].isActive)
    }

    @Test
    fun emptyStateWhenNoRules() = runTest {
        val recurringRepo = FakeRecurringRuleRepository(emptyList())
        val categoryRepo = FakeCategoryRepository(emptyList())
        val viewModel = RecurringViewModel(recurringRepo, categoryRepo)

        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isEmpty)
    }

    @Test
    fun togglePauseSetsRuleInactive() = runTest {
        val rule = recurringRule()
        val recurringRepo = FakeRecurringRuleRepository(listOf(rule))
        val categoryRepo = FakeCategoryRepository(listOf(category(9, "Internet & TV")))
        val viewModel = RecurringViewModel(recurringRepo, categoryRepo)

        advanceUntilIdle()
        viewModel.onTogglePause(rule.id)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.rules[0].isActive)
    }

    @Test
    fun togglePauseResumesInactiveRule() = runTest {
        val rule = recurringRule().copy(isActive = false)
        val recurringRepo = FakeRecurringRuleRepository(listOf(rule))
        val categoryRepo = FakeCategoryRepository(listOf(category(9, "Internet & TV")))
        val viewModel = RecurringViewModel(recurringRepo, categoryRepo)

        advanceUntilIdle()
        viewModel.onTogglePause(rule.id)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.rules[0].isActive)
    }

    @Test
    fun resumeAdvancesNextDueDatePastToday() = runTest {
        val pastDate = LocalDate.now().minusMonths(3)
        val rule = recurringRule(nextDueDate = pastDate, startDate = pastDate).copy(isActive = false)
        val recurringRepo = FakeRecurringRuleRepository(listOf(rule))
        val categoryRepo = FakeCategoryRepository(listOf(category(9, "Internet & TV")))
        val viewModel = RecurringViewModel(recurringRepo, categoryRepo)

        advanceUntilIdle()
        viewModel.onTogglePause(rule.id)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.rules[0].isActive)
        // nextDueDateLabel should NOT be "Overdue" since we advanced it
        assertFalse(state.rules[0].nextDueDateLabel == "Overdue")
    }

    @Test
    fun deleteRuleRemovesFromList() = runTest {
        val rule = recurringRule()
        val recurringRepo = FakeRecurringRuleRepository(listOf(rule))
        val categoryRepo = FakeCategoryRepository(listOf(category(9, "Internet & TV")))
        val viewModel = RecurringViewModel(recurringRepo, categoryRepo)

        advanceUntilIdle()
        viewModel.onDeleteRule(rule.id)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isEmpty)
    }

    @Test
    fun overdueRuleShowsOverdueLabel() = runTest {
        val pastDate = LocalDate.now().minusDays(5)
        val rule = recurringRule(nextDueDate = pastDate, startDate = pastDate)
        val recurringRepo = FakeRecurringRuleRepository(listOf(rule))
        val categoryRepo = FakeCategoryRepository(listOf(category(9, "Internet & TV")))
        val viewModel = RecurringViewModel(recurringRepo, categoryRepo)

        advanceUntilIdle()

        assertEquals("Overdue", viewModel.uiState.value.rules[0].nextDueDateLabel)
    }

    @Test
    fun pausedRuleShowsReducedAlpha() = runTest {
        val rule = recurringRule().copy(isActive = false)
        val recurringRepo = FakeRecurringRuleRepository(listOf(rule))
        val categoryRepo = FakeCategoryRepository(listOf(category(9, "Internet & TV")))
        val viewModel = RecurringViewModel(recurringRepo, categoryRepo)

        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.rules[0].isActive)
    }
}
