package com.example.clean.repositories

import com.example.clean.entities.CategoryExpenseStat
import com.example.clean.entities.DashboardSummary
import kotlinx.coroutines.flow.Flow

interface DashboardRepository {
    fun observeExpenseByCategory(start: Long, end: Long): Flow<List<CategoryExpenseStat>>
    fun observeSummary(start: Long, end: Long): Flow<DashboardSummary>
}