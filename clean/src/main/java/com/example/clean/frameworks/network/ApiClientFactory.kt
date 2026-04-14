package com.example.clean.frameworks.network

import com.example.clean.frameworks.auth.TokenStore
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

class ApiClientFactory(
    private val tokenStore: TokenStore
) {
    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    fun create(baseUrl: String = DEFAULT_BASE_URL): BudgetControlApi {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val token = runBlocking { tokenStore.getAccessToken() }
                val request = if (token.isNullOrBlank()) {
                    original
                } else {
                    original.newBuilder()
                        .header("Authorization", "Bearer $token")
                        .build()
                }
                val response = chain.proceed(request)
                if (response.code == 401) {
                    runBlocking { tokenStore.clear() }
                }
                response
            }
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(BudgetControlApi::class.java)
    }

    companion object {
        const val DEFAULT_BASE_URL = "http://192.168.1.27:5000/api/v1/"
    }
}
