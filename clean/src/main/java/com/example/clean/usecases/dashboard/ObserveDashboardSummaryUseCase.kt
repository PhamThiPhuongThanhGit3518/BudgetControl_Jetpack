package com.example.clean.usecases.dashboard

import com.example.clean.repositories.DashboardRepository

class ObserveDashboardSummaryUseCase(
    private val repository: DashboardRepository
) {
    operator fun invoke(start: Long, end: Long) = repository.observeSummary(start, end)
}