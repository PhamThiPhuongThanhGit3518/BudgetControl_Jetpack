package com.example.clean.adaptors.datasources.remote

import com.example.clean.frameworks.auth.TokenStore
import com.example.clean.frameworks.network.AuthRequestDto
import com.example.clean.frameworks.network.AuthResponseDto
import com.example.clean.frameworks.network.BudgetControlApi
import com.example.clean.frameworks.network.FirebaseLoginRequestDto
import kotlinx.coroutines.flow.Flow

class AuthRemoteDataSource(
    private val api: BudgetControlApi,
    private val tokenStore: TokenStore,
    private val syncAfterLogin: suspend () -> Unit
) {
    val accessToken: Flow<String?> = tokenStore.accessToken
    val displayName: Flow<String> = tokenStore.displayName

    suspend fun register(phoneNumber: String, password: String, displayName: String) {
        val response = api.register(
            AuthRequestDto(
                phoneNumber = phoneNumber,
                password = password,
                displayName = displayName
            )
        )
        saveSession(response)
        syncAfterLogin()
    }

    suspend fun login(phoneNumber: String, password: String) {
        val response = api.login(
            AuthRequestDto(
                phoneNumber = phoneNumber,
                password = password
            )
        )
        saveSession(response)
        syncAfterLogin()
    }

    suspend fun firebaseLogin(idToken: String) {
        val response = api.firebaseLogin(FirebaseLoginRequestDto(idToken))
        saveSession(response)
        syncAfterLogin()
    }

    suspend fun logout() {
        tokenStore.clear()
    }

    private suspend fun saveSession(response: AuthResponseDto) {
        val name = response.user.displayName
            .ifBlank { response.user.email }
            .ifBlank { response.user.phoneNumber }
            .ifBlank { "BudgetControl" }
        tokenStore.saveSession(response.accessToken, name)
    }
}
