package com.example.clean.adaptors.datasources.remote

import com.example.clean.frameworks.auth.TokenStore
import com.example.clean.frameworks.network.AuthRequestDto
import com.example.clean.frameworks.network.BudgetControlApi
import com.example.clean.frameworks.network.FirebaseLoginRequestDto
import kotlinx.coroutines.flow.Flow

class AuthRepository(
    private val api: BudgetControlApi,
    private val tokenStore: TokenStore,
    private val syncAfterLogin: suspend () -> Unit
) {
    val accessToken: Flow<String?> = tokenStore.accessToken

    suspend fun register(phoneNumber: String, password: String, displayName: String) {
        val response = api.register(
            AuthRequestDto(
                phoneNumber = phoneNumber,
                password = password,
                displayName = displayName
            )
        )
        tokenStore.saveAccessToken(response.accessToken)
        syncAfterLogin()
    }

    suspend fun login(phoneNumber: String, password: String) {
        val response = api.login(
            AuthRequestDto(
                phoneNumber = phoneNumber,
                password = password
            )
        )
        tokenStore.saveAccessToken(response.accessToken)
        syncAfterLogin()
    }

    suspend fun firebaseLogin(idToken: String) {
        val response = api.firebaseLogin(FirebaseLoginRequestDto(idToken))
        tokenStore.saveAccessToken(response.accessToken)
        syncAfterLogin()
    }

    suspend fun logout() {
        tokenStore.clear()
    }
}
