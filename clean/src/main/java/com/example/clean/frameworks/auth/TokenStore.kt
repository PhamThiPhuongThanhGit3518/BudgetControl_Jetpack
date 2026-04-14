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
    private val displayNameKey = stringPreferencesKey("display_name")

    val accessToken: Flow<String?> = context.authDataStore.data.map { preferences ->
        preferences[accessTokenKey]
    }

    val displayName: Flow<String> = context.authDataStore.data.map { preferences ->
        preferences[displayNameKey].orEmpty()
    }

    suspend fun getAccessToken(): String? = accessToken.first()

    suspend fun saveSession(token: String, displayName: String) {
        context.authDataStore.edit { preferences ->
            preferences[accessTokenKey] = token
            preferences[displayNameKey] = displayName
        }
    }

    suspend fun clear() {
        context.authDataStore.edit { preferences ->
            preferences.remove(accessTokenKey)
            preferences.remove(displayNameKey)
        }
    }
}
