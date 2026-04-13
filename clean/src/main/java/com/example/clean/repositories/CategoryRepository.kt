package com.example.clean.repositories

import com.example.clean.entities.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    suspend fun add(category: Category): Long
    suspend fun update(category: Category)
    suspend fun delete(category: Category)
    suspend fun getById(id: Long): Category?
    fun observeAll(): Flow<List<Category>>
    fun observeByType(type: String): Flow<List<Category>>
    suspend fun countTransactionsByCategoryId(categoryId: Long): Int
}