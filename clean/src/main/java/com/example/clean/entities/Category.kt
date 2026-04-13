package com.example.clean.entities

data class Category(
    val id: Long = 0,
    val name: String,
    val type: CategoryType,
    val colorHex: String,
    val icon: String,
    val isDefault: Boolean = false
)