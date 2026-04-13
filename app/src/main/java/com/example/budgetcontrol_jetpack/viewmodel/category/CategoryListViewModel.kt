package com.example.budgetcontrol_jetpack.viewmodel.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clean.entities.Category
import com.example.clean.usecases.category.CategoryUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CategoryListUiState(
    val isLoading: Boolean = true,
    val categories: List<Category> = emptyList(),
    val errorMessage: String? = null
)

class CategoryListViewModel(
    private val useCases: CategoryUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryListUiState())
    val uiState: StateFlow<CategoryListUiState> = _uiState.asStateFlow()

    init {
        observeCategories()
    }

    private fun observeCategories() {
        viewModelScope.launch {
            useCases.observeCategories().collect { items ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    categories = items,
                    errorMessage = null
                )
            }
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            runCatching {
                useCases.deleteCategory(category)
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