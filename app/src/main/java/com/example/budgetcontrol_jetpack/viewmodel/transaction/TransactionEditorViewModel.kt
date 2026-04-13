package com.example.budgetcontrol_jetpack.viewmodel.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clean.entities.Transaction
import com.example.clean.entities.TransactionType
import com.example.clean.frameworks.utils.DateTimeUtils
import com.example.clean.usecases.transaction.TransactionUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TransactionEditorUiState(
    val id: Long = 0,
    val title: String = "",
    val amount: String = "",
    val type: TransactionType = TransactionType.EXPENSE,
    val categoryId: Long = 0,
    val note: String = "",
    val errorMessage: String? = null
)

class TransactionEditorViewModel(
    private val useCases: TransactionUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionEditorUiState())
    val uiState: StateFlow<TransactionEditorUiState> = _uiState.asStateFlow()

    fun updateTitle(value: String) {
        _uiState.value = _uiState.value.copy(title = value)
    }

    fun updateAmount(value: String) {
        _uiState.value = _uiState.value.copy(amount = value)
    }

    fun updateNote(value: String) {
        _uiState.value = _uiState.value.copy(note = value)
    }

    fun updateCategory(categoryId: Long) {
        _uiState.value = _uiState.value.copy(categoryId = categoryId)
    }

    fun updateType(type: TransactionType) {
        _uiState.value = _uiState.value.copy(type = type)
    }

    fun load(id: Long) {
        if (id <= 0) return

        viewModelScope.launch {
            runCatching {
                useCases.getTransactionById(id)
            }.onSuccess { transaction ->
                if (transaction != null) {
                    _uiState.value = _uiState.value.copy(
                        id = transaction.id,
                        title = transaction.title,
                        amount = transaction.amount.toString(),
                        type = transaction.type,
                        categoryId = transaction.categoryId,
                        note = transaction.note,
                        errorMessage = null
                    )
                }
            }.onFailure { throwable ->
                _uiState.value = _uiState.value.copy(
                    errorMessage = throwable.message
                )
            }
        }
    }

    fun save(onDone: () -> Unit) {
        viewModelScope.launch {
            val state = _uiState.value
            val amountValue = state.amount.toDoubleOrNull() ?: 0.0

            val transaction = Transaction(
                id = state.id,
                title = state.title,
                amount = amountValue,
                type = state.type,
                categoryId = state.categoryId,
                note = state.note,
                createdAt = DateTimeUtils.nowMillis()
            )

            runCatching {
                if (state.id == 0L) {
                    useCases.addTransaction(transaction)
                } else {
                    useCases.updateTransaction(transaction)
                }
            }.onSuccess {
                onDone()
            }.onFailure { throwable ->
                _uiState.value = _uiState.value.copy(
                    errorMessage = throwable.message
                )
            }
        }
    }
}