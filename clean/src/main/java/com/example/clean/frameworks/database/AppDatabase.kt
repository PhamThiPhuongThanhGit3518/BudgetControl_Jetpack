package com.example.clean.frameworks.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.clean.frameworks.database.dao.CategoryDao
import com.example.clean.frameworks.database.dao.DashboardDao
import com.example.clean.frameworks.database.entity.CategoryLocalEntity
import com.example.clean.frameworks.database.dao.TransactionDao
import com.example.clean.frameworks.database.entity.TransactionLocalEntity

@Database(
    entities = [
        CategoryLocalEntity::class,
        TransactionLocalEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
    abstract fun dashboardDao(): DashboardDao
}