package com.example.clean.usecases.dashboard

data class DashboardUseCases(
    val observeDashboardSummary: ObserveDashboardSummaryUseCase,
    val observeExpenseStats: ObserveExpenseStatsUseCase
)