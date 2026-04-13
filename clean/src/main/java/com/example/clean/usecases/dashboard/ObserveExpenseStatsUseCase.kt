package com.example.clean.usecases.dashboard

import com.example.clean.repositories.DashboardRepository

class ObserveExpenseStatsUseCase(
    private val repository: DashboardRepository
) {
    operator fun invoke(start: Long, end: Long) =
        repository.observeExpenseByCategory(start, end)
}