package com.example.clean.adaptors.repositories

import com.example.clean.adaptors.datasources.local.datasource.DashboardLocalDataSource
import com.example.clean.entities.CategoryExpenseStat
import com.example.clean.entities.DashboardSummary
import com.example.clean.repositories.DashboardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class DashboardRepositoryImpl(
    private val localDataSource: DashboardLocalDataSource
) : DashboardRepository {

    override fun observeExpenseByCategory(
        start: Long,
        end: Long
    ): Flow<List<CategoryExpenseStat>> {
        return localDataSource.observeExpenseByCategory(start, end).map { rows ->
            val total = rows.sumOf { it.totalAmount }.takeIf { it > 0 } ?: 1.0
            rows.map {
                CategoryExpenseStat(
                    categoryId = it.categoryId,
                    categoryName = it.categoryName,
                    totalAmount = it.totalAmount,
                    ratio = (it.totalAmount / total).toFloat()
                )
            }
        }
    }

    override fun observeSummary(start: Long, end: Long): Flow<DashboardSummary> {
        return combine(
            localDataSource.observeTotalIncome(start, end),
            localDataSource.observeTotalExpense(start, end)
        ) { income, expense ->
            DashboardSummary(
                totalIncome = income,
                totalExpense = expense,
                balance = income - expense
            )
        }
    }
}