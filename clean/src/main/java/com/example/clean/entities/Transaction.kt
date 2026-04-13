package com.example.clean.entities

data class Transaction(
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val type: TransactionType,
    val categoryId: Long,
    val note: String = "",
    val createdAt: Long
)