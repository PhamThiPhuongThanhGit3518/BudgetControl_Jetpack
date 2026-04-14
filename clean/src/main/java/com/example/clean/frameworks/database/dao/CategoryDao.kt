package com.example.clean.frameworks.database.dao

import androidx.room.*
import com.example.clean.frameworks.database.entity.CategoryLocalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: CategoryLocalEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CategoryLocalEntity>)

    @Update
    suspend fun update(category: CategoryLocalEntity): Int

    @Delete
    suspend fun delete(category: CategoryLocalEntity): Int

    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun observeAll(): Flow<List<CategoryLocalEntity>>

    @Query("SELECT * FROM categories WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): CategoryLocalEntity?

    @Query("SELECT * FROM categories WHERE remoteId = :remoteId LIMIT 1")
    suspend fun getByRemoteId(remoteId: String): CategoryLocalEntity?

    @Query("SELECT * FROM categories")
    suspend fun getAllOnce(): List<CategoryLocalEntity>

    @Query("DELETE FROM categories")
    suspend fun clear()

    @Query("SELECT * FROM categories WHERE type = :type ORDER BY name ASC")
    fun observeByType(type: String): Flow<List<CategoryLocalEntity>>

    @Query("SELECT COUNT(*) FROM transactions WHERE categoryId = :categoryId")
    suspend fun countTransactionsByCategoryId(categoryId: Long): Int
}
