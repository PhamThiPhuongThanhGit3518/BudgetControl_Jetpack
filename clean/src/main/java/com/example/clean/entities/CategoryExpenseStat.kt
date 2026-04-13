package com.example.clean.entities

data class CategoryExpenseStat(
    val categoryId: Long,
    val categoryName: String,
    val totalAmount: Double,
    val ratio: Float
)