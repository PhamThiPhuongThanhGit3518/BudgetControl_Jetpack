package com.example.budgetcontrol_jetpack.ui.screen.category

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.budgetcontrol_jetpack.viewmodel.category.CategoryEditorViewModel
import com.example.clean.entities.CategoryType

@Composable
fun CategoryEditorScreen(
    viewModel: CategoryEditorViewModel,
    onDone: () -> Unit
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
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (uiState.id == 0L) "Thêm danh mục" else "Sửa danh mục",
                style = MaterialTheme.typography.headlineSmall
            )

            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::updateName,
                label = { Text("Tên danh mục") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = uiState.type == CategoryType.EXPENSE,
                    onClick = { viewModel.updateType(CategoryType.EXPENSE) },
                    label = { Text("Chi") }
                )
                FilterChip(
                    selected = uiState.type == CategoryType.INCOME,
                    onClick = { viewModel.updateType(CategoryType.INCOME) },
                    label = { Text("Thu") }
                )
            }

            OutlinedTextField(
                value = uiState.colorHex,
                onValueChange = viewModel::updateColor,
                label = { Text("Mã màu HEX") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("#FF9800") }
            )

            OutlinedTextField(
                value = uiState.icon,
                onValueChange = viewModel::updateIcon,
                label = { Text("Tên icon") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("restaurant") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.save(onDone) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (uiState.id == 0L) "Thêm danh mục" else "Lưu thay đổi")
            }
        }
    }
}