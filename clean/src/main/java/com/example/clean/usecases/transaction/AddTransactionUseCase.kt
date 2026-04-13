package com.example.clean.usecases.transaction

import com.example.clean.repositories.TransactionRepository
import com.example.clean.entities.Transaction

class AddTransactionUseCase(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(transaction: Transaction): Long {
        require(transaction.title.isNotBlank()) { "Tiêu đề không được rỗng" }
        require(transaction.amount > 0) { "Số tiền phải lớn hơn 0" }
        return repository.add(transaction)
    }
}