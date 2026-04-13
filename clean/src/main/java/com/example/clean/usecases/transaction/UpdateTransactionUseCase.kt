package com.example.clean.usecases.transaction

import com.example.clean.repositories.TransactionRepository
import com.example.clean.entities.Transaction

class UpdateTransactionUseCase(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(transaction: Transaction) {
        require(transaction.id > 0) { "ID không hợp lệ" }
        require(transaction.title.isNotBlank()) { "Tiêu đề không được rỗng" }
        require(transaction.amount > 0) { "Số tiền phải lớn hơn 0" }
        repository.update(transaction)
    }
}