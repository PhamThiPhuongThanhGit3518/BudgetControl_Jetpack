package com.example.budgetcontrol_jetpack.viewmodel.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clean.entities.Transaction
import com.example.clean.usecases.transaction.TransactionUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TransactionListUiState(
    val isLoading: Boolean = true,
    val transactions: List<Transaction> = emptyList(),
    val errorMessage: String? = null
)

class TransactionListViewModel(
    private val useCases: TransactionUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionListUiState())
    val uiState: StateFlow<TransactionListUiState> = _uiState.asStateFlow()

    init {
        observeTransactions()
    }

    private fun observeTransactions() {
        viewModelScope.launch {
            useCases.observeTransactions().collect { items ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    transactions = items,
                    errorMessage = null
                )
            }
        }
    }

    fun delete(transaction: Transaction) {
        viewModelScope.launch {
            runCatching {
                useCases.deleteTransaction(transaction)
            }.onFailure { throwable ->
                _uiState.value = _uiState.value.copy(
                    errorMessage = throwable.message
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}