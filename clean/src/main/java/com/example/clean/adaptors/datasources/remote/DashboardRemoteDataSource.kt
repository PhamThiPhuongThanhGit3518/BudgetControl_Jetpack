package com.example.clean.adaptors.datasources.remote

import com.example.clean.frameworks.network.BudgetControlApi
import com.example.clean.frameworks.network.DashboardSummaryDto
import com.example.clean.frameworks.network.ExpenseRatioDto

class DashboardRemoteDataSource(
    private val api: BudgetControlApi
) {
    suspend fun summary(period: String): DashboardSummaryDto =
        api.dashboardSummary(period)

    suspend fun expenseRatio(period: String): List<ExpenseRatioDto> =
        api.expenseRatio(period).items
}
