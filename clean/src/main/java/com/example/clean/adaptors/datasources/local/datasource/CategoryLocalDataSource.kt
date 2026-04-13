package com.example.clean.adaptors.datasources.local.datasource

import com.example.clean.frameworks.database.entity.CategoryLocalEntity
import com.example.clean.frameworks.database.dao.CategoryDao
import kotlinx.coroutines.flow.Flow

class CategoryLocalDataSource(
    private val dao: CategoryDao
) {
    suspend fun insert(category: CategoryLocalEntity): Long = dao.insert(category)

    suspend fun update(category: CategoryLocalEntity) = dao.update(category)

    suspend fun delete(category: CategoryLocalEntity) = dao.delete(category)

    fun observeAll(): Flow<List<CategoryLocalEntity>> = dao.observeAll()

    fun observeByType(type: String): Flow<List<CategoryLocalEntity>> = dao.observeByType(type)

    suspend fun getById(id: Long): CategoryLocalEntity? = dao.getById(id)

    suspend fun countTransactionsByCategoryId(categoryId: Long): Int =
        dao.countTransactionsByCategoryId(categoryId)
}