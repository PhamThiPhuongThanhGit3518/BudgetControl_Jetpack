package com.example.clean.adaptors.mapper

import com.example.clean.entities.CategoryExpenseStat
import com.example.clean.entities.DashboardSummary
import com.example.clean.frameworks.database.entity.CategoryLocalEntity
import com.example.clean.frameworks.database.entity.TransactionLocalEntity
import com.example.clean.frameworks.network.CategoryDto
import com.example.clean.frameworks.network.CategoryRequestDto
import com.example.clean.frameworks.network.DashboardSummaryDto
import com.example.clean.frameworks.network.ExpenseRatioDto
import com.example.clean.frameworks.network.TransactionDto
import com.example.clean.frameworks.network.TransactionRequestDto
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

fun CategoryDto.toLocal(existingId: Long = 0): CategoryLocalEntity {
    return CategoryLocalEntity(
        id = existingId,
        remoteId = id,
        name = name,
        type = type,
        colorHex = colorHex,
        icon = icon,
        isDefault = isDefault
    )
}

fun CategoryLocalEntity.toRequest(): CategoryRequestDto {
    return CategoryRequestDto(
        name = name,
        type = type,
        colorHex = colorHex,
        icon = icon,
        isDefault = isDefault
    )
}

fun TransactionDto.toLocal(categoryLocalId: Long, existingId: Long = 0): TransactionLocalEntity {
    return TransactionLocalEntity(
        id = existingId,
        remoteId = id,
        title = title,
        amount = amount,
        type = type,
        categoryId = categoryLocalId,
        note = note,
        createdAt = parseRemoteInstant(occurredAt).toEpochMilli()
    )
}

fun TransactionLocalEntity.toRequest(categoryRemoteId: String): TransactionRequestDto {
    return TransactionRequestDto(
        title = title,
        amount = amount,
        type = type,
        categoryId = categoryRemoteId,
        note = note,
        occurredAt = Instant.ofEpochMilli(createdAt).toString()
    )
}

fun DashboardSummaryDto.toDomain(): DashboardSummary {
    return DashboardSummary(
        totalIncome = totalIncome,
        totalExpense = totalExpense,
        balance = balance
    )
}

fun ExpenseRatioDto.toDomain(categoryLocalId: Long = 0): CategoryExpenseStat {
    return CategoryExpenseStat(
        categoryId = categoryLocalId,
        categoryName = categoryName,
        totalAmount = totalAmount,
        ratio = ratio
    )
}

private fun parseRemoteInstant(value: String): Instant {
    return runCatching {
        Instant.parse(value)
    }.getOrElse {
        LocalDateTime.parse(value).toInstant(ZoneOffset.UTC)
    }
}
