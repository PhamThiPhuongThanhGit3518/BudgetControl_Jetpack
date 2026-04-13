package com.example.clean.containers

import com.example.clean.usecases.category.*

class CategoryContainer(
    repositoryContainer: RepositoryContainer
) {
    val useCases = CategoryUseCases(
        addCategory = AddCategoryUseCase(repositoryContainer.categoryRepository),
        updateCategory = UpdateCategoryUseCase(repositoryContainer.categoryRepository),
        deleteCategory = DeleteCategoryUseCase(repositoryContainer.categoryRepository),
        getCategoryById = GetCategoryByIdUseCase(repositoryContainer.categoryRepository),
        observeCategories = ObserveCategoriesUseCase(repositoryContainer.categoryRepository),
        observeCategoriesByType = ObserveCategoriesByTypeUseCase(repositoryContainer.categoryRepository)
    )
}