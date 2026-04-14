package com.example.clean.containers

import android.content.Context
import androidx.room.Room
import com.example.clean.adaptors.datasources.local.*
import com.example.clean.adaptors.datasources.remote.AuthRemoteDataSource
import com.example.clean.adaptors.datasources.remote.CategoryRemoteDataSource
import com.example.clean.adaptors.datasources.remote.TransactionRemoteDataSource
import com.example.clean.adaptors.mapper.CategoryMapper
import com.example.clean.adaptors.mapper.TransactionMapper
import com.example.clean.adaptors.repositories.CategoryRepositoryImpl
import com.example.clean.adaptors.repositories.DashboardRepositoryImpl
import com.example.clean.adaptors.repositories.TransactionRepositoryImpl
import com.example.clean.frameworks.auth.TokenStore
import com.example.clean.frameworks.database.AppDatabase
import com.example.clean.frameworks.network.ApiClientFactory

class RepositoryContainer(context: Context) {

    private val database: AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "expense_manager.db"
    )
        .fallbackToDestructiveMigration()
        .build()

    val tokenStore = TokenStore(context)
    private val api = ApiClientFactory(tokenStore).create()

    private val categoryMapper = CategoryMapper()
    private val transactionMapper = TransactionMapper()
    private val categoryLocalDataSource = CategoryLocalDataSource(database.categoryDao())
    private val transactionLocalDataSource = TransactionLocalDataSource(database.transactionDao())
    private val dashboardLocalDataSource = DashboardLocalDataSource(database.dashboardDao())
    private val categoryRemoteDataSource = CategoryRemoteDataSource(api)
    private val transactionRemoteDataSource = TransactionRemoteDataSource(api)

    val categoryRepository = CategoryRepositoryImpl(
        localDataSource = categoryLocalDataSource,
        transactionLocalDataSource = transactionLocalDataSource,
        mapper = categoryMapper,
        remoteDataSource = categoryRemoteDataSource
    )

    val transactionRepository = TransactionRepositoryImpl(
        localDataSource = transactionLocalDataSource,
        categoryLocalDataSource = categoryLocalDataSource,
        mapper = transactionMapper,
        remoteDataSource = transactionRemoteDataSource
    )

    val dashboardRepository = DashboardRepositoryImpl(
        localDataSource = dashboardLocalDataSource
    )

    val authRepository = AuthRemoteDataSource(
        api = api,
        tokenStore = tokenStore,
        syncAfterLogin = {
            transactionRepository.clearCache()
            categoryRepository.clearCache()
            categoryRepository.syncFromRemote()
            transactionRepository.syncFromRemote()
        }
    )
}
