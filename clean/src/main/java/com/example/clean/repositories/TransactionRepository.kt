package com.example.clean.repositories

import com.example.clean.entities.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    suspend fun add(transaction: Transaction): Long
    suspend fun update(transaction: Transaction)
    suspend fun delete(transaction: Transaction)
    suspend fun getById(id: Long): Transaction?
    fun observeAll(): Flow<List<Transaction>>
    fun observeByRange(start: Long, end: Long): Flow<List<Transaction>>
    fun observeByType(type: String): Flow<List<Transaction>>
}