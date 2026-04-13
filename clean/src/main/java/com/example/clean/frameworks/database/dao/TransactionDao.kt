package com.example.clean.frameworks.database.dao

import androidx.room.*
import com.example.clean.frameworks.database.entity.TransactionLocalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionLocalEntity): Long

    @Update
    suspend fun update(transaction: TransactionLocalEntity): Int

    @Delete
    suspend fun delete(transaction: TransactionLocalEntity): Int

    @Query("SELECT * FROM transactions ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<TransactionLocalEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): TransactionLocalEntity?

    @Query("""
        SELECT * FROM transactions
        WHERE createdAt BETWEEN :start AND :end
        ORDER BY createdAt DESC
    """)
    fun observeByRange(start: Long, end: Long): Flow<List<TransactionLocalEntity>>

    @Query("""
        SELECT * FROM transactions
        WHERE type = :type
        ORDER BY createdAt DESC
    """)
    fun observeByType(type: String): Flow<List<TransactionLocalEntity>>
}
