package com.example.clean.usecases.category

import com.example.clean.entities.Category
import com.example.clean.repositories.CategoryRepository
import kotlinx.coroutines.flow.Flow

class ObserveCategoriesUseCase(
    private val repository: CategoryRepository
) {
    operator fun invoke(): Flow<List<Category>> {
        return repository.observeAll()
    }
}