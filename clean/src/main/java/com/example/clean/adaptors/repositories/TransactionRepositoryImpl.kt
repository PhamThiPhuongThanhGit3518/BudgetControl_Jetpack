package com.example.clean.adaptors.repositories

import com.example.clean.adaptors.datasources.local.datasource.TransactionLocalDataSource
import com.example.clean.adaptors.datasources.local.datasource.CategoryLocalDataSource
import com.example.clean.adaptors.mapper.TransactionMapper
import com.example.clean.adaptors.mapper.toLocal
import com.example.clean.adaptors.mapper.toRequest
import com.example.clean.entities.Transaction
import com.example.clean.frameworks.network.BudgetControlApi
import com.example.clean.repositories.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionRepositoryImpl(
    private val localDataSource: TransactionLocalDataSource,
    private val categoryLocalDataSource: CategoryLocalDataSource,
    private val mapper: TransactionMapper,
    private val api: BudgetControlApi? = null
) : TransactionRepository {

    override suspend fun add(transaction: Transaction): Long {
        val local = mapper.toLocal(transaction)
        val remote = api?.createTransaction(local.toRequest(resolveCategoryRemoteId(local.categoryId)))
        return if (remote != null) {
            upsertRemote(remote)
        } else {
            localDataSource.insert(local)
        }
    }

    override suspend fun update(transaction: Transaction) {
        val current = localDataSource.getById(transaction.id)
        val local = mapper.toLocal(transaction).copy(remoteId = current?.remoteId)
        val remoteId = current?.remoteId
        if (api != null && remoteId != null) {
            upsertRemote(api.updateTransaction(remoteId, local.toRequest(resolveCategoryRemoteId(local.categoryId))))
        } else {
            localDataSource.update(local)
        }
    }

    override suspend fun delete(transaction: Transaction) {
        val current = localDataSource.getById(transaction.id)
        val remoteId = current?.remoteId
        if (api != null && remoteId != null) {
            api.deleteTransaction(remoteId)
        }
        localDataSource.delete(mapper.toLocal(transaction).copy(remoteId = remoteId))
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

    suspend fun syncFromRemote() {
        val remoteItems = api?.listTransactions()?.items ?: return
        localDataSource.clear()
        remoteItems.forEach { upsertRemote(it) }
    }

    suspend fun clearCache() {
        localDataSource.clear()
    }

    private suspend fun resolveCategoryRemoteId(categoryId: Long): String {
        val category = categoryLocalDataSource.getById(categoryId)
        return category?.remoteId ?: error("Danh mục chưa được đồng bộ, vui lòng đăng nhập hoặc tải lại dữ liệu")
    }

    private suspend fun upsertRemote(remote: com.example.clean.frameworks.network.TransactionDto): Long {
        val category = categoryLocalDataSource.getByRemoteId(remote.categoryId)
            ?: return 0
        val existing = localDataSource.getByRemoteId(remote.id)
        return localDataSource.insert(remote.toLocal(category.id, existing?.id ?: 0))
    }
}
