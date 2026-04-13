package com.example.budgetcontrol_jetpack.ui.screen.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.budgetcontrol_jetpack.viewmodel.dashboard.DashboardViewModel
import com.example.clean.entities.DatePeriod

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel
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
            Text(
                text = "Thống kê",
                style = MaterialTheme.typography.headlineSmall
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = uiState.period == DatePeriod.WEEK,
                    onClick = { viewModel.updatePeriod(DatePeriod.WEEK) },
                    label = { Text("Tuần") }
                )
                FilterChip(
                    selected = uiState.period == DatePeriod.MONTH,
                    onClick = { viewModel.updatePeriod(DatePeriod.MONTH) },
                    label = { Text("Tháng") }
                )
                FilterChip(
                    selected = uiState.period == DatePeriod.YEAR,
                    onClick = { viewModel.updatePeriod(DatePeriod.YEAR) },
                    label = { Text("Năm") }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("Tổng thu: ${uiState.summary.totalIncome}")
            Text("Tổng chi: ${uiState.summary.totalExpense}")
            Text("Số dư: ${uiState.summary.balance}")

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Chi tiêu theo danh mục",
                style = MaterialTheme.typography.titleMedium
            )

            if (uiState.stats.isEmpty()) {
                Text("Chưa có dữ liệu")
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.stats.forEach { item ->
                        Text("${item.categoryName}: ${item.totalAmount}")
                    }
                }
            }
        }
    }
}