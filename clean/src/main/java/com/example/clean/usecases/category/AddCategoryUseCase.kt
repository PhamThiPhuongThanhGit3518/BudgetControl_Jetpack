package com.example.clean.usecases.category

import com.example.clean.entities.Category
import com.example.clean.repositories.CategoryRepository

class AddCategoryUseCase(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(category: Category): Long {
        require(category.name.isNotBlank()) { "Tên danh mục không được rỗng" }
        require(category.colorHex.isNotBlank()) { "Màu danh mục không được rỗng" }
        require(category.icon.isNotBlank()) { "Icon danh mục không được rỗng" }

        return repository.add(
            category.copy(
                name = category.name.trim()
            )
        )
    }
}