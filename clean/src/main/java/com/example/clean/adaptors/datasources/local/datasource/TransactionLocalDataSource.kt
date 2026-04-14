package com.example.clean.adaptors.datasources.local.datasource

import com.example.clean.frameworks.database.entity.TransactionLocalEntity
import com.example.clean.frameworks.database.dao.TransactionDao
import kotlinx.coroutines.flow.Flow

class TransactionLocalDataSource(
    private val dao: TransactionDao
) {
    suspend fun insert(transaction: TransactionLocalEntity): Long = dao.insert(transaction)

    suspend fun insertAll(transactions: List<TransactionLocalEntity>) = dao.insertAll(transactions)

    suspend fun update(transaction: TransactionLocalEntity) = dao.update(transaction)

    suspend fun delete(transaction: TransactionLocalEntity) = dao.delete(transaction)

    fun observeAll(): Flow<List<TransactionLocalEntity>> = dao.observeAll()

    fun observeByRange(start: Long, end: Long): Flow<List<TransactionLocalEntity>> =
        dao.observeByRange(start, end)

    fun observeByType(type: String): Flow<List<TransactionLocalEntity>> =
        dao.observeByType(type)

    suspend fun getById(id: Long): TransactionLocalEntity? = dao.getById(id)

    suspend fun getByRemoteId(remoteId: String): TransactionLocalEntity? = dao.getByRemoteId(remoteId)

    suspend fun clear() = dao.clear()
}
