package com.example.clean.adaptors.datasources.remote

import com.example.clean.frameworks.network.BudgetControlApi
import com.example.clean.frameworks.network.CategoryDto
import com.example.clean.frameworks.network.CategoryRequestDto

class CategoryRemoteDataSource(
    private val api: BudgetControlApi
) {
    suspend fun list(): List<CategoryDto> = api.listCategories().items

    suspend fun create(body: CategoryRequestDto): CategoryDto = api.createCategory(body)

    suspend fun update(id: String, body: CategoryRequestDto): CategoryDto =
        api.updateCategory(id, body)

    suspend fun delete(id: String) {
        api.deleteCategory(id)
    }
}
