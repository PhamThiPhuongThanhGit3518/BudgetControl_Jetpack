package com.example.clean.frameworks.network

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface BudgetControlApi {
    @POST("auth/register")
    suspend fun register(@Body body: AuthRequestDto): AuthResponseDto

    @POST("auth/login")
    suspend fun login(@Body body: AuthRequestDto): AuthResponseDto

    @POST("auth/firebase-login")
    suspend fun firebaseLogin(@Body body: FirebaseLoginRequestDto): AuthResponseDto

    @GET("auth/me")
    suspend fun me(): UserEnvelopeDto

    @GET("categories")
    suspend fun listCategories(@Query("type") type: String? = null): ListResponseDto<CategoryDto>

    @POST("categories")
    suspend fun createCategory(@Body body: CategoryRequestDto): CategoryDto

    @PUT("categories/{id}")
    suspend fun updateCategory(@Path("id") id: String, @Body body: CategoryRequestDto): CategoryDto

    @DELETE("categories/{id}")
    suspend fun deleteCategory(@Path("id") id: String)

    @GET("transactions")
    suspend fun listTransactions(
        @Query("categoryId") categoryId: String? = null,
        @Query("type") type: String? = null
    ): ListResponseDto<TransactionDto>

    @POST("transactions")
    suspend fun createTransaction(@Body body: TransactionRequestDto): TransactionDto

    @PUT("transactions/{id}")
    suspend fun updateTransaction(@Path("id") id: String, @Body body: TransactionRequestDto): TransactionDto

    @DELETE("transactions/{id}")
    suspend fun deleteTransaction(@Path("id") id: String)

    @GET("dashboard/summary")
    suspend fun dashboardSummary(@Query("period") period: String): DashboardSummaryDto

    @GET("dashboard/expense-ratio")
    suspend fun expenseRatio(@Query("period") period: String): ListResponseDto<ExpenseRatioDto>
}

@kotlinx.serialization.Serializable
data class UserEnvelopeDto(
    val user: UserDto
)
