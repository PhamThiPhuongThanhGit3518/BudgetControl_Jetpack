package com.example.clean.containers

import android.content.Context
import androidx.room.Room
import com.example.clean.adaptors.datasources.local.*
import com.example.clean.adaptors.datasources.local.datasource.*
import com.example.clean.adaptors.mapper.CategoryMapper
import com.example.clean.adaptors.mapper.TransactionMapper
import com.example.clean.adaptors.repositories.CategoryRepositoryImpl
import com.example.clean.adaptors.repositories.DashboardRepositoryImpl
import com.example.clean.adaptors.repositories.TransactionRepositoryImpl
import com.example.clean.frameworks.database.AppDatabase

class RepositoryContainer(context: Context) {

    private val database: AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "expense_manager.db"
    ).build()

    private val categoryMapper = CategoryMapper()
    private val transactionMapper = TransactionMapper()

    val categoryRepository = CategoryRepositoryImpl(
        localDataSource = CategoryLocalDataSource(database.categoryDao()),
        mapper = categoryMapper
    )

    val transactionRepository = TransactionRepositoryImpl(
        localDataSource = TransactionLocalDataSource(database.transactionDao()),
        mapper = transactionMapper
    )

    val dashboardRepository = DashboardRepositoryImpl(
        localDataSource = DashboardLocalDataSource(database.dashboardDao())
    )
}