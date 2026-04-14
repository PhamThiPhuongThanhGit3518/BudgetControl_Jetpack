package com.example.clean.frameworks.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "categories",
    indices = [Index(value = ["remoteId"], unique = true)]
)
data class CategoryLocalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val remoteId: String? = null,
    val name: String,
    val type: String,
    val isDefault: Boolean
)
