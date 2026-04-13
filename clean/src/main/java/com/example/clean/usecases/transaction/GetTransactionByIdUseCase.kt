package com.example.clean.usecases.transaction

import com.example.clean.repositories.TransactionRepository
import com.example.clean.entities.Transaction

class GetTransactionByIdUseCase(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(id: Long): Transaction? = repository.getById(id)
}