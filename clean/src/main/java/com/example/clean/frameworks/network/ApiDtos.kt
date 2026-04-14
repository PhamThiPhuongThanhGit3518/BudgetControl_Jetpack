package com.example.clean.frameworks.network

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequestDto(
    val phoneNumber: String,
    val password: String,
    val displayName: String? = null
)

@Serializable
data class FirebaseLoginRequestDto(
    val idToken: String
)

@Serializable
data class AuthResponseDto(
    val message: String,
    val accessToken: String,
    val user: UserDto
)

@Serializable
data class UserDto(
    val id: String,
    val firebaseUid: String = "",
    val email: String = "",
    val displayName: String = "",
    val photoUrl: String = "",
    val phoneNumber: String = "",
    val authProviders: List<String> = emptyList(),
    val provider: String = "",
    val isActive: Boolean = true
)

@Serializable
data class ListResponseDto<T>(
    val items: List<T>
)

@Serializable
data class CategoryDto(
    val id: String,
    val name: String,
    val type: String,
    val isDefault: Boolean = false
)

@Serializable
data class CategoryRequestDto(
    val name: String,
    val type: String,
    val isDefault: Boolean = false
)

@Serializable
data class TransactionDto(
    val id: String,
    val title: String,
    val amount: Double,
    val type: String,
    val categoryId: String,
    val categoryName: String = "",
    val note: String = "",
    val occurredAt: String
)

@Serializable
data class TransactionRequestDto(
    val title: String,
    val amount: Double,
    val type: String,
    val categoryId: String,
    val note: String = "",
    val occurredAt: String
)

@Serializable
data class DashboardSummaryDto(
    val totalIncome: Double,
    val totalExpense: Double,
    val balance: Double,
    val period: String
)

@Serializable
data class ExpenseRatioDto(
    val categoryId: String,
    val categoryName: String,
    val totalAmount: Double,
    val ratio: Float
)
