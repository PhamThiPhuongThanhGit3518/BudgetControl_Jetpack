package com.example.budgetcontrol_jetpack.viewmodel.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clean.entities.Category
import com.example.clean.entities.CategoryType
import com.example.clean.usecases.category.CategoryUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CategoryEditorUiState(
    val id: Long = 0,
    val name: String = "",
    val type: CategoryType = CategoryType.EXPENSE,
    val colorHex: String = "#FF9800",
    val icon: String = "category",
    val isDefault: Boolean = false,
    val errorMessage: String? = null
)

class CategoryEditorViewModel(
    private val useCases: CategoryUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryEditorUiState())
    val uiState: StateFlow<CategoryEditorUiState> = _uiState.asStateFlow()

    fun updateName(value: String) {
        _uiState.value = _uiState.value.copy(name = value, errorMessage = null)
    }

    fun updateType(value: CategoryType) {
        _uiState.value = _uiState.value.copy(type = value, errorMessage = null)
    }

    fun updateColor(value: String) {
        _uiState.value = _uiState.value.copy(colorHex = value)
    }

    fun updateIcon(value: String) {
        _uiState.value = _uiState.value.copy(icon = value)
    }

    fun load(id: Long) {
        if (id <= 0) return

        viewModelScope.launch {
            runCatching {
                useCases.getCategoryById(id)
            }.onSuccess { category ->
                if (category != null) {
                    _uiState.value = _uiState.value.copy(
                        id = category.id,
                        name = category.name,
                        type = category.type,
                        colorHex = category.colorHex,
                        icon = category.icon,
                        isDefault = category.isDefault,
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

            if (state.name.isBlank()) {
                _uiState.value = state.copy(errorMessage = "Vui lòng nhập tên danh mục")
                return@launch
            }

            val category = Category(
                id = state.id,
                name = state.name.trim(),
                type = state.type,
                colorHex = state.colorHex,
                icon = state.icon,
                isDefault = state.isDefault
            )

            runCatching {
                if (state.id == 0L) {
                    useCases.addCategory(category)
                } else {
                    useCases.updateCategory(category)
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

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
