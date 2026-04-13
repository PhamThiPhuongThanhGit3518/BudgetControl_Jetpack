package com.example.clean.adaptors.repositories

import com.example.clean.adaptors.datasources.local.datasource.TransactionLocalDataSource
import com.example.clean.adaptors.mapper.TransactionMapper
import com.example.clean.entities.Transaction
import com.example.clean.repositories.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionRepositoryImpl(
    private val localDataSource: TransactionLocalDataSource,
    private val mapper: TransactionMapper
) : TransactionRepository {

    override suspend fun add(transaction: Transaction): Long {
        return localDataSource.insert(mapper.toLocal(transaction))
    }

    override suspend fun update(transaction: Transaction) {
        localDataSource.update(mapper.toLocal(transaction))
    }

    override suspend fun delete(transaction: Transaction) {
        localDataSource.delete(mapper.toLocal(transaction))
    }

    override suspend fun getById(id: Long): Transaction? {
        return localDataSource.getById(id)?.let(mapper::toDomain)
    }

    override fun observeAll(): Flow<List<Transaction>> {
        return localDataSource.observeAll().map { list ->
            list.map(mapper::toDomain)
        }
    }

    override fun observeByRange(start: Long, end: Long): Flow<List<Transaction>> {
        return localDataSource.observeByRange(start, end).map { list ->
            list.map(mapper::toDomain)
        }
    }

    override fun observeByType(type: String): Flow<List<Transaction>> {
        return localDataSource.observeByType(type).map { list ->
            list.map(mapper::toDomain)
        }
    }
}