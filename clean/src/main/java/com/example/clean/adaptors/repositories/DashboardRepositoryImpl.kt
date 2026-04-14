package com.example.clean.adaptors.repositories

import com.example.clean.adaptors.datasources.local.DashboardLocalDataSource
import com.example.clean.adaptors.datasources.remote.DashboardRemoteDataSource
import com.example.clean.adaptors.mapper.toDomain
import com.example.clean.entities.CategoryExpenseStat
import com.example.clean.entities.DashboardSummary
import com.example.clean.repositories.DashboardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit

class DashboardRepositoryImpl(
    private val localDataSource: DashboardLocalDataSource,
    private val remoteDataSource: DashboardRemoteDataSource? = null
) : DashboardRepository {

    override fun observeExpenseByCategory(
        start: Long,
        end: Long
    ): Flow<List<CategoryExpenseStat>> {
        if (remoteDataSource != null) {
            return flow {
                emit(remoteDataSource.expenseRatio(periodFromRange(start, end)).map { it.toDomain() })
            }
        }
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
        if (remoteDataSource != null) {
            return flow {
                emit(remoteDataSource.summary(periodFromRange(start, end)).toDomain())
            }
        }
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

    private fun periodFromRange(start: Long, end: Long): String {
        val days = TimeUnit.MILLISECONDS.toDays(end - start) + 1
        return when {
            days <= 7 -> "week"
            days <= 31 -> "month"
            else -> "year"
        }
    }
}
