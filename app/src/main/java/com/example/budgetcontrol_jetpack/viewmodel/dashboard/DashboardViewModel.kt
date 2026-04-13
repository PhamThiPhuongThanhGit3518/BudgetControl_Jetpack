package com.example.budgetcontrol_jetpack.viewmodel.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clean.entities.CategoryExpenseStat
import com.example.clean.entities.DashboardSummary
import com.example.clean.entities.DatePeriod
import com.example.clean.usecases.dashboard.DashboardUseCases
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

data class DashboardUiState(
    val period: DatePeriod = DatePeriod.MONTH,
    val periodLabel: String = "",
    val summary: DashboardSummary = DashboardSummary(0.0, 0.0, 0.0),
    val stats: List<CategoryExpenseStat> = emptyList()
)

private data class DashboardFilter(
    val period: DatePeriod,
    val anchorDate: LocalDate
)

private data class DateRange(
    val startMillis: Long,
    val endMillis: Long,
    val label: String
)

class DashboardViewModel(
    private val useCases: DashboardUseCases
) : ViewModel() {

    private val filterFlow = MutableStateFlow(
        DashboardFilter(
            period = DatePeriod.MONTH,
            anchorDate = LocalDate.now()
        )
    )

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        observeDashboard()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeDashboard() {
        viewModelScope.launch {
            filterFlow.flatMapLatest { filter ->
                val range = filter.toDateRange()

                combine(
                    useCases.observeDashboardSummary(range.startMillis, range.endMillis),
                    useCases.observeExpenseStats(range.startMillis, range.endMillis)
                ) { summary, stats ->
                    DashboardUiState(
                        period = filter.period,
                        periodLabel = range.label,
                        summary = summary,
                        stats = stats
                    )
                }
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun updatePeriod(period: DatePeriod) {
        filterFlow.value = DashboardFilter(
            period = period,
            anchorDate = LocalDate.now()
        )
    }

    fun movePrevious() {
        val current = filterFlow.value
        filterFlow.value = current.copy(
            anchorDate = current.anchorDate.shiftByPeriod(current.period, -1)
        )
    }

    fun moveNext() {
        val current = filterFlow.value
        filterFlow.value = current.copy(
            anchorDate = current.anchorDate.shiftByPeriod(current.period, 1)
        )
    }

    private fun DashboardFilter.toDateRange(): DateRange {
        val start = when (period) {
            DatePeriod.WEEK -> anchorDate.with(DayOfWeek.MONDAY)
            DatePeriod.MONTH -> anchorDate.withDayOfMonth(1)
            DatePeriod.YEAR -> anchorDate.withDayOfYear(1)
        }
        val end = when (period) {
            DatePeriod.WEEK -> start.plusDays(6)
            DatePeriod.MONTH -> start.plusMonths(1).minusDays(1)
            DatePeriod.YEAR -> start.plusYears(1).minusDays(1)
        }

        return DateRange(
            startMillis = start.startOfDayMillis(),
            endMillis = end.endOfDayMillis(),
            label = formatRangeLabel(period, start, end)
        )
    }

    private fun LocalDate.shiftByPeriod(period: DatePeriod, amount: Long): LocalDate {
        return when (period) {
            DatePeriod.WEEK -> plusWeeks(amount)
            DatePeriod.MONTH -> plusMonths(amount)
            DatePeriod.YEAR -> plusYears(amount)
        }
    }

    private fun LocalDate.startOfDayMillis(): Long {
        return atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    private fun LocalDate.endOfDayMillis(): Long {
        return plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() - 1
    }

    private fun formatRangeLabel(
        period: DatePeriod,
        start: LocalDate,
        end: LocalDate
    ): String {
        val locale = Locale.forLanguageTag("vi-VN")
        val dayFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", locale)

        return when (period) {
            DatePeriod.WEEK -> "${start.format(dayFormatter)} - ${end.format(dayFormatter)}"
            DatePeriod.MONTH -> start.format(DateTimeFormatter.ofPattern("MM/yyyy", locale))
            DatePeriod.YEAR -> start.format(DateTimeFormatter.ofPattern("yyyy", locale))
        }
    }
}
