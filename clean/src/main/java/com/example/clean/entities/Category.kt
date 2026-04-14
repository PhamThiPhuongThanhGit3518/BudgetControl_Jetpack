package com.example.clean.entities

data class Category(
    val id: Long = 0,
    val name: String,
    val type: CategoryType,
    val isDefault: Boolean = false
)
