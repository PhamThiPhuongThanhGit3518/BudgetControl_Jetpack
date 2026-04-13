package com.example.clean.adaptors.mapper

import com.example.clean.frameworks.database.entity.TransactionLocalEntity
import com.example.clean.entities.Transaction
import com.example.clean.entities.TransactionType

class TransactionMapper : Mapper<TransactionLocalEntity, Transaction> {

    override fun toDomain(local: TransactionLocalEntity): Transaction {
        return Transaction(
            id = local.id,
            title = local.title,
            amount = local.amount,
            type =                                                                                                                                                      TransactionType.valueOf(local.type),
            categoryId = local.categoryId,
            note = local.note,
            createdAt = local.createdAt
        )
    }

    override fun toLocal(domain: Transaction): TransactionLocalEntity {
        return TransactionLocalEntity(
            id = domain.id,
            title = domain.title,
            amount = domain.amount,
            type = domain.type.name,
            categoryId = domain.categoryId,
            note = domain.note,
            createdAt = domain.createdAt
        )
    }
}