package com.example.budgetcontrol_jetpack.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clean.adaptors.datasources.remote.AuthRemoteDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val displayName: String = "",
    val errorMessage: String? = null
)

class AuthViewModel(
    private val authRepository: AuthRemoteDataSource
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.accessToken.collect { token ->
                _uiState.value = _uiState.value.copy(isAuthenticated = !token.isNullOrBlank())
            }
        }
        viewModelScope.launch {
            authRepository.displayName.collect { displayName ->
                _uiState.value = _uiState.value.copy(displayName = displayName)
            }
        }
    }

    fun login(phoneNumber: String, password: String) {
        launchAuth {
            authRepository.login(phoneNumber, password)
        }
    }

    fun register(phoneNumber: String, password: String, displayName: String) {
        launchAuth {
            authRepository.register(phoneNumber, password, displayName)
        }
    }

    fun firebaseLogin(idToken: String) {
        launchAuth {
            authRepository.firebaseLogin(idToken)
        }
    }

    fun logout() {
        _uiState.value = _uiState.value.copy(
            isAuthenticated = false,
            isLoading = false,
            displayName = "",
            errorMessage = null
        )
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun showError(message: String) {
        _uiState.value = _uiState.value.copy(errorMessage = message, isLoading = false)
    }

    private fun launchAuth(block: suspend () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            runCatching {
                block()
            }.onFailure { throwable ->
                _uiState.value = _uiState.value.copy(
                    errorMessage = throwable.message ?: "Không thể đăng nhập"
                )
            }
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }
}
