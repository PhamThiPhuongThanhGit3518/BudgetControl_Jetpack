package com.example.clean.adaptors.datasources.remote

import com.example.clean.frameworks.network.BudgetControlApi
import com.example.clean.frameworks.network.TransactionDto
import com.example.clean.frameworks.network.TransactionRequestDto

class TransactionRemoteDataSource(
    private val api: BudgetControlApi
) {
    suspend fun list(): List<TransactionDto> = api.listTransactions().items

    suspend fun create(body: TransactionRequestDto): TransactionDto =
        api.createTransaction(body)

    suspend fun update(id: String, body: TransactionRequestDto): TransactionDto =
        api.updateTransaction(id, body)

    suspend fun delete(id: String) {
        api.deleteTransaction(id)
    }
}
