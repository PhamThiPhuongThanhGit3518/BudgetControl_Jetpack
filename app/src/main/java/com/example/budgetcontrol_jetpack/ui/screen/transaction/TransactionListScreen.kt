package com.example.budgetcontrol_jetpack.ui.screen.transaction

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.budgetcontrol_jetpack.viewmodel.transaction.TransactionListViewModel

@Composable
fun TransactionListScreen(
    viewModel: TransactionListViewModel,
    onAddClick: () -> Unit,
    onEditClick: (Long) -> Unit,
    onGoDashboard: () -> Unit,
    onGoCategories: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Text("+")
            }
        }
    ) { padding ->
        if (uiState.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        text = "Giao dịch",
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                item {
                    Button(
                        onClick = onGoDashboard,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Xem thống kê")
                    }
                }

                item {
                    Button(
                        onClick = onGoCategories,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Quản lý danh mục")
                    }
                }

                items(uiState.transactions) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onEditClick(item.id) }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = item.title, style = MaterialTheme.typography.titleMedium)
                            Text(text = "Số tiền: ${item.amount}")
                            Text(text = "Loại: ${item.type.name}")
                            Text(text = "CategoryId: ${item.categoryId}")
                            Text(text = "Ghi chú: ${item.note}")

                            TextButton(onClick = { viewModel.delete(item) }) {
                                Text("Xóa")
                            }
                        }
                    }
                }
            }
        }
    }
}