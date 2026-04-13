package com.example.clean.adaptors.repositories

import com.example.clean.adaptors.datasources.local.datasource.CategoryLocalDataSource
import com.example.clean.adaptors.mapper.CategoryMapper
import com.example.clean.entities.Category
import com.example.clean.repositories.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoryRepositoryImpl(
    private val localDataSource: CategoryLocalDataSource,
    private val mapper: CategoryMapper
) : CategoryRepository {

    override suspend fun add(category: Category): Long {
        return localDataSource.insert(mapper.toLocal(category))
    }

    override suspend fun update(category: Category) {
        localDataSource.update(mapper.toLocal(category))
    }

    override suspend fun delete(category: Category) {
        localDataSource.delete(mapper.toLocal(category))
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
}