package com.example.budgetcontrol_jetpack.viewmodel.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clean.entities.Category
import com.example.clean.entities.CategoryType
import com.example.clean.entities.Transaction
import com.example.clean.entities.TransactionType
import com.example.clean.frameworks.utils.DateTimeUtils
import com.example.clean.usecases.category.CategoryUseCases
import com.example.clean.usecases.transaction.TransactionUseCases
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

data class TransactionEditorUiState(
    val id: Long = 0,
    val title: String = "",
    val amount: String = "",
    val type: TransactionType = TransactionType.EXPENSE,
    val categoryId: Long = 0,
    val categories: List<Category> = emptyList(),
    val note: String = "",
    val errorMessage: String? = null
)

class TransactionEditorViewModel(
    private val useCases: TransactionUseCases,
    private val categoryUseCases: CategoryUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionEditorUiState())
    val uiState: StateFlow<TransactionEditorUiState> = _uiState.asStateFlow()

    init {
        observeCategories()
    }

    fun updateTitle(value: String) {
        _uiState.value = _uiState.value.copy(title = value)
    }

    fun updateAmount(value: String) {
        val digits = value.filter { it.isDigit() }
        _uiState.value = _uiState.value.copy(
            amount = formatAmountInput(digits),
            errorMessage = null
        )
    }

    fun updateNote(value: String) {
        _uiState.value = _uiState.value.copy(note = value)
    }

    fun updateCategory(categoryId: Long) {
        _uiState.value = _uiState.value.copy(
            categoryId = categoryId,
            errorMessage = null
        )
    }

    fun updateType(type: TransactionType) {
        _uiState.value = _uiState.value.copy(
            type = type,
            categoryId = 0,
            errorMessage = null
        )
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
                        amount = formatAmountInput(transaction.amount.toLong().toString()),
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
            val amountValue = state.amount.replace(".", "").toDoubleOrNull() ?: 0.0
            val selectedCategory = state.categories.firstOrNull { it.id == state.categoryId }

            if (amountValue <= 0.0) {
                _uiState.value = state.copy(errorMessage = "Vui lòng nhập số tiền")
                return@launch
            }

            if (selectedCategory == null) {
                _uiState.value = state.copy(errorMessage = "Vui lòng chọn danh mục")
                return@launch
            }

            val transaction = Transaction(
                id = state.id,
                title = selectedCategory.name,
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

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeCategories() {
        viewModelScope.launch {
            _uiState
                .map { it.type }
                .distinctUntilChanged()
                .flatMapLatest { state ->
                    categoryUseCases.observeCategoriesByType(
                        CategoryType.valueOf(state.name)
                    )
                }
                .collect { categories ->
                    val current = _uiState.value
                    val selectedCategoryId = when {
                        categories.any { it.id == current.categoryId } -> current.categoryId
                        categories.isNotEmpty() -> categories.first().id
                        else -> 0L
                    }

                    _uiState.value = current.copy(
                        categories = categories,
                        categoryId = selectedCategoryId
                    )
                }
        }
    }

    private fun formatAmountInput(digits: String): String {
        if (digits.isBlank()) return ""

        return digits
            .trimStart('0')
            .ifBlank { "0" }
            .reversed()
            .chunked(3)
            .joinToString(".")
            .reversed()
    }
}
