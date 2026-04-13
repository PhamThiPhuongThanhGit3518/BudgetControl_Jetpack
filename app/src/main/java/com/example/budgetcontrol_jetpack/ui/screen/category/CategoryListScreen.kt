package com.example.budgetcontrol_jetpack.ui.screen.category

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.budgetcontrol_jetpack.ui.screen.category.component.CategoryItem
import com.example.budgetcontrol_jetpack.viewmodel.category.CategoryListViewModel

@Composable
fun CategoryListScreen(
    viewModel: CategoryListViewModel,
    onAddClick: () -> Unit,
    onCategoryClick: (Long) -> Unit,
    onEditClick: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

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
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddBox,
                            contentDescription = null
                        )
                        Text(
                            text = "  Thêm danh mục mới",
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                }

                if (uiState.categories.isEmpty()) {
                    item {
                        Text(
                            text = "Chưa có danh mục nào",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                items(uiState.categories, key = { it.id }) { category ->
                    CategoryItem(
                        category = category,
                        onClick = { onCategoryClick(category.id) },
                        onEditClick = { onEditClick(category.id) },
                        onDeleteClick = { viewModel.deleteCategory(category) }
                    )
                }
            }
        }
    }
}

private val ScreenBackground = Color(0xFFFBF5FD)
private val AccentBlue = Color(0xFF0F5697)
