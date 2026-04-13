package com.example.clean.usecases.transaction

data class TransactionUseCases(
    val addTransaction: AddTransactionUseCase,
    val updateTransaction: UpdateTransactionUseCase,
    val deleteTransaction: DeleteTransactionUseCase,
    val getTransactionById: GetTransactionByIdUseCase,
    val observeTransactions: ObserveTransactionsUseCase
)