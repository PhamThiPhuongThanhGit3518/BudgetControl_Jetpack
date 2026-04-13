package com.example.clean.frameworks.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.clean.entities.DashboardExpenseRow
import kotlinx.coroutines.flow.Flow

@Dao
interface DashboardDao {

    @Query("""
        SELECT 
            c.id AS categoryId,
            c.name AS categoryName,
            SUM(t.amount) AS totalAmount
        FROM transactions t
        INNER JOIN categories c ON c.id = t.categoryId
        WHERE t.type = 'EXPENSE'
          AND t.createdAt BETWEEN :start AND :end
        GROUP BY c.id, c.name
        ORDER BY totalAmount DESC
    """)
    fun observeExpenseByCategory(
        start: Long,
        end: Long
    ): Flow<List<DashboardExpenseRow>>

    @Query("""
        SELECT COALESCE(SUM(amount), 0)
        FROM transactions
        WHERE type = 'INCOME'
          AND createdAt BETWEEN :start AND :end
    """)
    fun observeTotalIncome(start: Long, end: Long): Flow<Double>

    @Query("""
        SELECT COALESCE(SUM(amount), 0)
        FROM transactions
        WHERE type = 'EXPENSE'
          AND createdAt BETWEEN :start AND :end
    """)
    fun observeTotalExpense(start: Long, end: Long): Flow<Double>
}