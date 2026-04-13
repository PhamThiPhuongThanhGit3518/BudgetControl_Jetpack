package com.example.budgetcontrol_jetpack.ui.screen.transaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.budgetcontrol_jetpack.viewmodel.transaction.TransactionEditorViewModel
import com.example.clean.entities.TransactionType

@Composable
fun TransactionEditorScreen(
    viewModel: TransactionEditorViewModel,
    onDone: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = uiState.title,
                onValueChange = viewModel::updateTitle,
                label = { Text("Tiêu đề") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.amount,
                onValueChange = viewModel::updateAmount,
                label = { Text("Số tiền") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = uiState.type == TransactionType.EXPENSE,
                    onClick = { viewModel.updateType(TransactionType.EXPENSE) },
                    label = { Text("Chi") }
                )

                FilterChip(
                    selected = uiState.type == TransactionType.INCOME,
                    onClick = { viewModel.updateType(TransactionType.INCOME) },
                    label = { Text("Thu") }
                )
            }

            OutlinedTextField(
                value = uiState.note,
                onValueChange = viewModel::updateNote,
                label = { Text("Ghi chú") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { viewModel.save(onDone) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Lưu")
            }
        }
    }
}