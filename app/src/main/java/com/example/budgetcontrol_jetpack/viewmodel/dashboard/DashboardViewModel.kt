package com.example.budgetcontrol_jetpack.viewmodel.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clean.entities.CategoryExpenseStat
import com.example.clean.entities.DashboardSummary
import com.example.clean.entities.DatePeriod
import com.example.clean.frameworks.utils.DateTimeUtils
import com.example.clean.usecases.dashboard.DashboardUseCases
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

data class DashboardUiState(
    val period: DatePeriod = DatePeriod.MONTH,
    val summary: DashboardSummary = DashboardSummary(0.0, 0.0, 0.0),
    val stats: List<CategoryExpenseStat> = emptyList()
)

class DashboardViewModel(
    private val useCases: DashboardUseCases
) : ViewModel() {

    private val periodFlow = MutableStateFlow(DatePeriod.MONTH)

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        observeDashboard()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeDashboard() {
        viewModelScope.launch {
            periodFlow.flatMapLatest { period ->
                val start = when (period) {
                    DatePeriod.WEEK -> DateTimeUtils.startOfCurrentWeek()
                    DatePeriod.MONTH -> DateTimeUtils.startOfCurrentMonth()
                    DatePeriod.YEAR -> DateTimeUtils.startOfCurrentYear()
                }
                val end = DateTimeUtils.endOfToday()

                combine(
                    useCases.observeDashboardSummary(start, end),
                    useCases.observeExpenseStats(start, end)
                ) { summary, stats ->
                    DashboardUiState(
                        period = period,
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
        periodFlow.value = period
    }
}
