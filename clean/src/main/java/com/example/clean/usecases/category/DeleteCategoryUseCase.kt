package com.example.clean.usecases.category

import com.example.clean.entities.Category
import com.example.clean.repositories.CategoryRepository

class DeleteCategoryUseCase(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(category: Category) {
        require(category.id > 0) { "ID danh mục không hợp lệ" }

        val count = repository.countTransactionsByCategoryId(category.id)
        require(count == 0) { "Danh mục đang có dữ liệu, không thể xóa" }

        repository.delete(category)
    }
}