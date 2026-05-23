package com.pulsefinance.presentation.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulsefinance.domain.model.CategorySpend
import com.pulsefinance.domain.model.DashboardSnapshot
import com.pulsefinance.domain.model.Expense
import com.pulsefinance.domain.model.Money
import com.pulsefinance.domain.usecase.ObserveDashboardUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val observeDashboard: ObserveDashboardUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState

    private var observeJob: Job? = null
    private var currentPeriod: AnalyticsPeriod = AnalyticsPeriod.ThisMonth

    init {
        observePeriod(currentPeriod)
    }

    fun onPeriodSelected(period: AnalyticsPeriod) {
        currentPeriod = period
        _uiState.value = _uiState.value.copy(periodLabel = period.label)
        observePeriod(period)
    }

    fun onTabSelected(tab: AnalyticsTab) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }

    private fun observePeriod(period: AnalyticsPeriod) {
        observeJob?.cancel()
        val month = when (period) {
            AnalyticsPeriod.ThisWeek -> YearMonth.now()
            AnalyticsPeriod.ThisMonth -> YearMonth.now()
            AnalyticsPeriod.LastMonth -> YearMonth.now().minusMonths(1)
        }
        observeJob = viewModelScope.launch {
            observeDashboard(month, RECENT_LIMIT)
                .catch {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
                .collect { snapshot ->
                    _uiState.value = snapshot.toUiState(period)
                }
        }
    }

    private fun DashboardSnapshot.toUiState(period: AnalyticsPeriod): AnalyticsUiState {
        val totalMinor = monthlySpend.amountMinor
        val breakdown = categorySpending.map { it.toBreakdownItem(totalMinor) }
        val accessibilitySummary = buildAccessibilitySummary(monthlySpend, breakdown)

        return AnalyticsUiState(
            isLoading = false,
            periodLabel = period.label,
            selectedTab = _uiState.value.selectedTab,
            totalSpend = monthlySpend.format(),
            categoryBreakdown = breakdown,
            recentTransactions = recentTransactions.map { it.toTransactionItem() },
            chartAccessibilitySummary = accessibilitySummary,
        )
    }

    private fun CategorySpend.toBreakdownItem(totalMinor: Long): CategoryBreakdownItem {
        val pct = if (totalMinor > 0) (amount.amountMinor * 100 / totalMinor).toInt() else 0
        val sweep = if (totalMinor > 0) (amount.amountMinor.toFloat() / totalMinor * 360f) else 0f
        return CategoryBreakdownItem(
            categoryId = category.id,
            name = category.name,
            colorHex = category.colorHex,
            amount = amount.format(),
            percent = pct,
            sweepAngle = sweep,
        )
    }

    private fun Expense.toTransactionItem(): AnalyticsTransactionItem {
        return AnalyticsTransactionItem(
            id = id,
            merchant = merchant ?: title,
            categoryName = "",
            categoryColorHex = "",
            amount = "-${amount.format()}",
            dateLabel = formatDateLabel(expenseDate),
        )
    }

    private fun buildAccessibilitySummary(total: Money, breakdown: List<CategoryBreakdownItem>): String {
        if (breakdown.isEmpty()) return "No spending data available."
        val items = breakdown.joinToString(". ") { "${it.name}: ${it.amount}, ${it.percent}%" }
        return "Total spend: ${total.format()}. $items"
    }

    private fun formatDateLabel(date: LocalDate): String {
        val today = LocalDate.now()
        return when {
            date == today -> "Today"
            date == today.minusDays(1) -> "Yesterday"
            date.year == today.year -> date.format(SHORT_DATE_FORMAT)
            else -> date.format(FULL_DATE_FORMAT)
        }
    }

    companion object {
        private const val RECENT_LIMIT = 5
        private val SHORT_DATE_FORMAT = DateTimeFormatter.ofPattern("MMM d")
        private val FULL_DATE_FORMAT = DateTimeFormatter.ofPattern("MMM d, yyyy")
    }
}
