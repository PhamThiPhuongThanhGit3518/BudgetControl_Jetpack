package com.example.clean.usecases.category

import com.example.clean.entities.Category
import com.example.clean.entities.CategoryType
import com.example.clean.repositories.CategoryRepository
import kotlinx.coroutines.flow.Flow

class ObserveCategoriesByTypeUseCase(
    private val repository: CategoryRepository
) {
    operator fun invoke(type: CategoryType): Flow<List<Category>> {
        return repository.observeByType(type.name)
    }
}