package com.example.clean.adaptors.datasources.local

import com.example.clean.frameworks.database.dao.DashboardDao

class DashboardLocalDataSource(
    private val dao: DashboardDao
) {
    fun observeExpenseByCategory(start: Long, end: Long) =
        dao.observeExpenseByCategory(start, end)

    fun observeTotalIncome(start: Long, end: Long) =
        dao.observeTotalIncome(start, end)

    fun observeTotalExpense(start: Long, end: Long) =
        dao.observeTotalExpense(start, end)
}
