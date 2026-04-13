package com.example.clean.usecases.category

import com.example.clean.entities.Category
import com.example.clean.repositories.CategoryRepository

class GetCategoryByIdUseCase(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(id: Long): Category? {
        require(id > 0) { "ID danh mục không hợp lệ" }
        return repository.getById(id)
    }
}