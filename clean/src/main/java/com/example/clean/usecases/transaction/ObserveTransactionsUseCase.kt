package com.example.clean.usecases.transaction

import com.example.clean.entities.Transaction
import com.example.clean.repositories.TransactionRepository
import kotlinx.coroutines.flow.Flow

class ObserveTransactionsUseCase(
    private val repository: TransactionRepository
) {
    operator fun invoke(): Flow<List<Transaction>> = repository.observeAll()
}