package com.example.clean.containers

import com.example.clean.usecases.transaction.AddTransactionUseCase
import com.example.clean.usecases.transaction.DeleteTransactionUseCase
import com.example.clean.usecases.transaction.GetTransactionByIdUseCase
import com.example.clean.usecases.transaction.ObserveTransactionsUseCase
import com.example.clean.usecases.transaction.TransactionUseCases
import com.example.clean.usecases.transaction.UpdateTransactionUseCase

class TransactionContainer(
    repositoryContainer: RepositoryContainer
) {
    val useCases = TransactionUseCases(
        addTransaction = AddTransactionUseCase(repositoryContainer.transactionRepository),
        updateTransaction = UpdateTransactionUseCase(repositoryContainer.transactionRepository),
        deleteTransaction = DeleteTransactionUseCase(repositoryContainer.transactionRepository),
        getTransactionById = GetTransactionByIdUseCase(repositoryContainer.transactionRepository),
        observeTransactions = ObserveTransactionsUseCase(repositoryContainer.transactionRepository)
    )
}