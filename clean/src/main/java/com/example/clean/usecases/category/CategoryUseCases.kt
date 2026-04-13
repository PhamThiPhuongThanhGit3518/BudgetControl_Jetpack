package com.example.clean.usecases.category

data class CategoryUseCases(
    val addCategory: AddCategoryUseCase,
    val updateCategory: UpdateCategoryUseCase,
    val deleteCategory: DeleteCategoryUseCase,
    val getCategoryById: GetCategoryByIdUseCase,
    val observeCategories: ObserveCategoriesUseCase,
    val observeCategoriesByType: ObserveCategoriesByTypeUseCase
)