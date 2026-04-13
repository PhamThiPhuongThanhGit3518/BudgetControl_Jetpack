package com.example.budgetcontrol_jetpack.ui.screen.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.budgetcontrol_jetpack.viewmodel.dashboard.DashboardViewModel
import com.example.clean.entities.CategoryExpenseStat
import com.example.clean.entities.DatePeriod
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"))

    Scaffold(containerColor = ScreenBackground) { padding ->
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

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard(
                    title = "Tổng thu",
                    value = currencyFormatter.format(uiState.summary.totalIncome),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Tổng chi",
                    value = currencyFormatter.format(uiState.summary.totalExpense),
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                text = "Chi tiêu theo danh mục",
                style = MaterialTheme.typography.titleMedium
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = Color.White,
                shadowElevation = 1.dp
            ) {
                if (uiState.stats.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .padding(16.dp)
                    ) {
                        Text("Chưa có dữ liệu")
                    }
                } else {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ExpenseBarChart(stats = uiState.stats)
                        uiState.stats.forEach { item ->
                            Text(
                                text = "${item.categoryName}: ${currencyFormatter.format(item.totalAmount)}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MutedText
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF2A2D34)
            )
        }
    }
}

@Composable
private fun ExpenseBarChart(stats: List<CategoryExpenseStat>) {
    val chartColors = listOf(
        Color(0xFF4FA6A9),
        Color(0xFF5D9B55),
        Color(0xFF8069C9),
        Color(0xFF2B73B7),
        Color(0xFFE54535),
        Color(0xFFD39A2B)
    )
    val maxAmount = stats.maxOfOrNull { it.totalAmount }?.takeIf { it > 0.0 } ?: 1.0

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(Color.White)
    ) {
        val leftPadding = 22.dp.toPx()
        val bottomPadding = 22.dp.toPx()
        val topPadding = 8.dp.toPx()
        val chartHeight = size.height - topPadding - bottomPadding
        val chartWidth = size.width - leftPadding

        repeat(4) { index ->
            val y = topPadding + chartHeight * index / 3
            drawLine(
                color = Color(0xFFE7E2EB),
                start = Offset(leftPadding, y),
                end = Offset(size.width, y),
                strokeWidth = 1.dp.toPx()
            )
        }

        val visibleStats = stats.take(6)
        val itemWidth = chartWidth / visibleStats.size.coerceAtLeast(1)
        val barWidth = itemWidth * 0.46f

        visibleStats.forEachIndexed { index, item ->
            val barHeight = (item.totalAmount / maxAmount).toFloat() * chartHeight
            val x = leftPadding + itemWidth * index + (itemWidth - barWidth) / 2
            val y = topPadding + chartHeight - barHeight

            drawRect(
                color = chartColors[index % chartColors.size],
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight)
            )
        }
    }
}

private val ScreenBackground = Color(0xFFFBF5FD)
private val MutedText = Color(0xFF77737D)
