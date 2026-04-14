package com.example.clean.adaptors.repositories

import com.example.clean.adaptors.datasources.local.datasource.CategoryLocalDataSource
import com.example.clean.adaptors.mapper.CategoryMapper
import com.example.clean.adaptors.mapper.toLocal
import com.example.clean.adaptors.mapper.toRequest
import com.example.clean.entities.Category
import com.example.clean.frameworks.network.BudgetControlApi
import com.example.clean.repositories.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoryRepositoryImpl(
    private val localDataSource: CategoryLocalDataSource,
    private val mapper: CategoryMapper,
    private val api: BudgetControlApi? = null
) : CategoryRepository {

    override suspend fun add(category: Category): Long {
        val local = mapper.toLocal(category)
        val remote = api?.createCategory(local.toRequest())
        return if (remote != null) {
            upsertRemote(remote)
        } else {
            localDataSource.insert(local)
        }
    }

    override suspend fun update(category: Category) {
        val current = localDataSource.getById(category.id)
        val local = mapper.toLocal(category).copy(remoteId = current?.remoteId)
        val remoteId = current?.remoteId
        if (api != null && remoteId != null) {
            upsertRemote(api.updateCategory(remoteId, local.toRequest()))
        } else {
            localDataSource.update(local)
        }
    }

    override suspend fun delete(category: Category) {
        val current = localDataSource.getById(category.id)
        val remoteId = current?.remoteId
        if (api != null && remoteId != null) {
            api.deleteCategory(remoteId)
        }
        localDataSource.delete(mapper.toLocal(category).copy(remoteId = remoteId))
    }

    override suspend fun getById(id: Long): Category? {
        return localDataSource.getById(id)?.let(mapper::toDomain)
    }

    override fun observeAll(): Flow<List<Category>> {
        return localDataSource.observeAll().map { list ->
            list.map(mapper::toDomain)
        }
    }

    override fun observeByType(type: String): Flow<List<Category>> {
        return localDataSource.observeByType(type).map { list ->
            list.map(mapper::toDomain)
        }
    }

    override suspend fun countTransactionsByCategoryId(categoryId: Long): Int {
        return localDataSource.countTransactionsByCategoryId(categoryId)
    }

    suspend fun syncFromRemote() {
        val remoteItems = api?.listCategories()?.items ?: return
        remoteItems.forEach { upsertRemote(it) }
    }

    suspend fun clearCache() {
        localDataSource.clear()
    }

    private suspend fun upsertRemote(remote: com.example.clean.frameworks.network.CategoryDto): Long {
        val existing = localDataSource.getByRemoteId(remote.id)
        return localDataSource.insert(remote.toLocal(existing?.id ?: 0))
    }
}
