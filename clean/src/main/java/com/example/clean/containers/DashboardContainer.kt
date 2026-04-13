package com.example.clean.containers

import com.example.clean.usecases.dashboard.DashboardUseCases
import com.example.clean.usecases.dashboard.ObserveDashboardSummaryUseCase
import com.example.clean.usecases.dashboard.ObserveExpenseStatsUseCase

class DashboardContainer(
    repositoryContainer: RepositoryContainer
) {
    val useCases = DashboardUseCases(
        observeDashboardSummary = ObserveDashboardSummaryUseCase(
            repositoryContainer.dashboardRepository
        ),
        observeExpenseStats = ObserveExpenseStatsUseCase(
            repositoryContainer.dashboardRepository
        )
    )
}