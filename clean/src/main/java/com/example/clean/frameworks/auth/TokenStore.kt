package com.example.clean.frameworks.auth

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.authDataStore by preferencesDataStore(name = "auth")

class TokenStore(
    private val context: Context
) {
    private val accessTokenKey = stringPreferencesKey("access_token")

    val accessToken: Flow<String?> = context.authDataStore.data.map { preferences ->
        preferences[accessTokenKey]
    }

    suspend fun getAccessToken(): String? = accessToken.first()

    suspend fun saveAccessToken(token: String) {
        context.authDataStore.edit { preferences ->
            preferences[accessTokenKey] = token
        }
    }

    suspend fun clear() {
        context.authDataStore.edit { preferences ->
            preferences.remove(accessTokenKey)
        }
    }
}
