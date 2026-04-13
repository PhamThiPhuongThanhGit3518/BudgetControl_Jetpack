package com.example.clean.usecases.category

import com.example.clean.entities.Category
import com.example.clean.repositories.CategoryRepository

class UpdateCategoryUseCase(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(category: Category) {
        require(category.id > 0) { "ID danh mục không hợp lệ" }
        require(category.name.isNotBlank()) { "Tên danh mục không được rỗng" }
        require(category.colorHex.isNotBlank()) { "Màu danh mục không được rỗng" }
        require(category.icon.isNotBlank()) { "Icon danh mục không được rỗng" }

        repository.update(
            category.copy(
                name = category.name.trim()
            )
        )
    }
}