package com.example.clean.usecases.transaction

import com.example.clean.repositories.TransactionRepository
import com.example.clean.entities.Transaction

class DeleteTransactionUseCase(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(transaction: Transaction) {
        repository.delete(transaction)
    }
}