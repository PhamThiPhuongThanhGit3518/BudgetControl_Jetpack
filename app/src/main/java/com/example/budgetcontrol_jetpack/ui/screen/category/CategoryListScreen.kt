package com.example.budgetcontrol_jetpack.ui.screen.category

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.budgetcontrol_jetpack.R
import com.example.budgetcontrol_jetpack.ui.theme.BudgetControl_JetpackTheme
import com.example.budgetcontrol_jetpack.ui.screen.category.component.CategoryItem
import com.example.budgetcontrol_jetpack.viewmodel.category.CategoryListUiState
import com.example.budgetcontrol_jetpack.viewmodel.category.CategoryListViewModel
import com.example.clean.entities.Category
import com.example.clean.entities.CategoryType

@Composable
fun CategoryListScreen(
    viewModel: CategoryListViewModel,
    onAddClick: () -> Unit,
    onCategoryClick: (Category) -> Unit,
    onEditClick: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var categoryPendingDelete by remember { mutableStateOf<Category?>(null) }

    LaunchedEffect(uiState.errorMessage) {
        val message = uiState.errorMessage
        if (!message.isNullOrBlank()) {
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
        }
    }

    Scaffold(
        containerColor = ScreenBackground,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { padding ->
        CategoryListContent(
            uiState = uiState,
            padding = padding,
            onAddClick = onAddClick,
            onCategoryClick = onCategoryClick,
            onEditClick = onEditClick,
            onDeleteClick = { categoryPendingDelete = it }
        )

        categoryPendingDelete?.let { category ->
            DeleteCategoryConfirmDialog(
                categoryName = category.name,
                onDismiss = { categoryPendingDelete = null },
                onConfirm = {
                    viewModel.deleteCategory(category)
                    categoryPendingDelete = null
                }
            )
        }
    }
}

@Composable
private fun CategoryListContent(
    uiState: CategoryListUiState,
    padding: androidx.compose.foundation.layout.PaddingValues,
    onAddClick: () -> Unit,
    onCategoryClick: (Category) -> Unit,
    onEditClick: (Long) -> Unit,
    onDeleteClick: (Category) -> Unit
) {
    if (uiState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Button(
                    onClick = onAddClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentBlue,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(
                        top = 16.dp,
                        bottom = 16.dp,
                        start = 24.dp,
                        end = 24.dp
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.AddBox,
                        contentDescription = null
                    )
                    Text(
                        text = stringResource(R.string.category_list_add_new),
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }

            if (uiState.categories.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.category_list_empty),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            items(uiState.categories, key = { it.id }) { category ->
                CategoryItem(
                    category = category,
                    onClick = { onCategoryClick(category) },
                    onEditClick = { onEditClick(category.id) },
                    onDeleteClick = { onDeleteClick(category) }
                )
            }
        }
    }
}

private val ScreenBackground = Color(0xFFFBF5FD)
private val AccentBlue = Color(0xFF0F5697)

@Preview(showBackground = true, widthDp = 360, heightDp = 760)
@Composable
private fun CategoryListScreenPreview() {
    BudgetControl_JetpackTheme(dynamicColor = false) {
        Scaffold(containerColor = ScreenBackground) { padding ->
            CategoryListContent(
                uiState = CategoryListUiState(
                    isLoading = false,
                    categories = listOf(
                        Category(1, "Ăn uống", CategoryType.EXPENSE, false),
                        Category(2, "Lương", CategoryType.INCOME, false)
                    )
                ),
                padding = padding,
                onAddClick = {},
                onCategoryClick = {},
                onEditClick = {},
                onDeleteClick = {}
            )
        }
    }
}
