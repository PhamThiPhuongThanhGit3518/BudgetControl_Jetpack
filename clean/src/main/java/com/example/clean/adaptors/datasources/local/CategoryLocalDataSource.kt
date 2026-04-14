package com.example.clean.adaptors.datasources.local

import com.example.clean.frameworks.database.entity.CategoryLocalEntity
import com.example.clean.frameworks.database.dao.CategoryDao
import kotlinx.coroutines.flow.Flow

class CategoryLocalDataSource(
    private val dao: CategoryDao
) {
    suspend fun insert(category: CategoryLocalEntity): Long = dao.insert(category)

    suspend fun insertAll(categories: List<CategoryLocalEntity>) = dao.insertAll(categories)

    suspend fun update(category: CategoryLocalEntity) = dao.update(category)

    suspend fun delete(category: CategoryLocalEntity) = dao.delete(category)

    fun observeAll(): Flow<List<CategoryLocalEntity>> = dao.observeAll()

    fun observeByType(type: String): Flow<List<CategoryLocalEntity>> = dao.observeByType(type)

    suspend fun getById(id: Long): CategoryLocalEntity? = dao.getById(id)

    suspend fun getByRemoteId(remoteId: String): CategoryLocalEntity? = dao.getByRemoteId(remoteId)

    suspend fun getAllOnce(): List<CategoryLocalEntity> = dao.getAllOnce()

    suspend fun clear() = dao.clear()

    suspend fun countTransactionsByCategoryId(categoryId: Long): Int =
        dao.countTransactionsByCategoryId(categoryId)
}
