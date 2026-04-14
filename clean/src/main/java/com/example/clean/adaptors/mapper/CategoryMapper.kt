package com.example.clean.adaptors.mapper

import com.example.clean.frameworks.database.entity.CategoryLocalEntity
import com.example.clean.entities.Category
import com.example.clean.entities.CategoryType


class CategoryMapper : Mapper<CategoryLocalEntity, Category> {

    override fun toDomain(local: CategoryLocalEntity): Category {
        return Category(
            id = local.id,
            name = local.name,
            type = CategoryType.valueOf(local.type),
            colorHex = local.colorHex,
            icon = local.icon,
            isDefault = local.isDefault
        )
    }

    override fun toLocal(domain: Category): CategoryLocalEntity {
        return CategoryLocalEntity(
            id = domain.id,
            remoteId = null,
            name = domain.name,
            type = domain.type.name,
            colorHex = domain.colorHex,
            icon = domain.icon,
            isDefault = domain.isDefault
        )
    }
}
