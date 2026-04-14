package com.example.budgetcontrol_jetpack.ui.screen.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Black
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

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = Color.White,
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = viewModel::movePrevious) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "Thời gian trước",
                            tint = Color.Black
                        )
                    }

                    Text(
                        text = uiState.periodLabel,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black
                    )

                    IconButton(onClick = viewModel::moveNext) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Thời gian sau",
                            tint = Color.Black
                        )
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard(
                    title = "Tổng thu",
                    value = currencyFormatter.format(uiState.summary.totalIncome),
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Tổng chi",
                    value = currencyFormatter.format(uiState.summary.totalExpense),
                    color = Color(0xFFF44336),
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                text = "Chi tiêu theo danh mục",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF000000)
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
                        ExpensePieChart(stats = uiState.stats)
                        uiState.stats.take(6).forEachIndexed { index, item ->
                            ChartLegendRow(
                                color = ChartColors[index % ChartColors.size],
                                label = item.categoryName,
                                value = currencyFormatter.format(item.totalAmount)
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
    color: Color = Color(0xFF2A2D34),
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
                color = color
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
private fun ExpensePieChart(stats: List<CategoryExpenseStat>) {
    val visibleStats = stats.take(6)
    val totalAmount = visibleStats.sumOf { it.totalAmount }.takeIf { it > 0.0 } ?: 1.0

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
            .background(Color.White)
    ) {
        val diameter = minOf(size.width, size.height) * 0.84f
        val topLeft = Offset(
            x = (size.width - diameter) / 2f,
            y = (size.height - diameter) / 2f
        )
        var startAngle = -90f

        visibleStats.forEachIndexed { index, item ->
            val sweepAngle = (item.totalAmount / totalAmount).toFloat() * 360f

            drawArc(
                color = ChartColors[index % ChartColors.size],
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                topLeft = topLeft,
                size = Size(diameter, diameter)
            )
            startAngle += sweepAngle
        }

        drawCircle(
            color = Color.White,
            radius = diameter * 0.26f,
            center = Offset(size.width / 2f, size.height / 2f)
        )
    }
}

@Composable
private fun ChartLegendRow(
    color: Color,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, CircleShape)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF000000),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MutedText
        )
    }
}

private val ScreenBackground = Color(0xFFFBF5FD)
private val MutedText = Color(0xFF77737D)
private val ChartColors = listOf(
    Color(0xFF4FA6A9),
    Color(0xFF5D9B55),
    Color(0xFF8069C9),
    Color(0xFF2B73B7),
    Color(0xFFE54535),
    Color(0xFFD39A2B)
)
