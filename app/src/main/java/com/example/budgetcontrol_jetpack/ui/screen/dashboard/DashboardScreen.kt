package com.example.budgetcontrol_jetpack.ui.screen.dashboard

import android.widget.NumberPicker
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.budgetcontrol_jetpack.R
import com.example.budgetcontrol_jetpack.ui.theme.BudgetControl_JetpackTheme
import com.example.budgetcontrol_jetpack.viewmodel.dashboard.DashboardUiState
import com.example.budgetcontrol_jetpack.viewmodel.dashboard.DashboardViewModel
import com.example.clean.entities.CategoryExpenseStat
import com.example.clean.entities.DashboardSummary
import com.example.clean.entities.DatePeriod
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onIncomeClick: () -> Unit,
    onExpenseClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showYearPicker by remember { mutableStateOf(false) }
    var showMonthPicker by remember { mutableStateOf(false) }
    var showDateRangePicker by remember { mutableStateOf(false) }
    DashboardScreenContent(
        uiState = uiState,
        onCustomClick = { viewModel.updatePeriod(DatePeriod.CUSTOM) },
        onMonthClick = { viewModel.updatePeriod(DatePeriod.MONTH) },
        onYearClick = { viewModel.updatePeriod(DatePeriod.YEAR) },
        onPrevious = viewModel::movePrevious,
        onNext = viewModel::moveNext,
        onValueClick = {
            when (uiState.period) {
                DatePeriod.MONTH -> showMonthPicker = true
                DatePeriod.YEAR -> showYearPicker = true
                DatePeriod.CUSTOM -> showDateRangePicker = true
            }
        },
        onIncomeClick = onIncomeClick,
        onExpenseClick = onExpenseClick
    )

    if (showYearPicker) {
        YearPickerDialog(
            initialYear = uiState.anchorYear,
            onDismiss = { showYearPicker = false },
            onConfirm = { year ->
                viewModel.updateYear(year)
                showYearPicker = false
            }
        )
    }

    if (showMonthPicker) {
        MonthYearPickerDialog(
            initialMonth = uiState.anchorMonth,
            initialYear = uiState.anchorYear,
            onDismiss = { showMonthPicker = false },
            onConfirm = { month, year ->
                viewModel.updateMonth(year = year, month = month)
                showMonthPicker = false
            }
        )
    }

    if (showDateRangePicker) {
        val compactDateFormatter = remember {
            SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("vi-VN"))
        }
        val pickerState = rememberDateRangePickerState(
            initialSelectedStartDateMillis = uiState.startMillis.takeIf { it > 0L },
            initialSelectedEndDateMillis = uiState.endMillis.takeIf { it > 0L }
        )
        DatePickerDialog(
            onDismissRequest = { showDateRangePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val start = pickerState.selectedStartDateMillis?.toLocalDate()
                        val end = pickerState.selectedEndDateMillis?.toLocalDate()
                        if (start != null && end != null) {
                            viewModel.updateCustomRange(start, end)
                        }
                        showDateRangePicker = false
                    },
                    enabled = pickerState.selectedStartDateMillis != null &&
                        pickerState.selectedEndDateMillis != null
                ) {
                    Text(stringResource(R.string.dashboard_apply))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDateRangePicker = false }) {
                    Text(stringResource(R.string.close))
                }
            }
        ) {
            DateRangePicker(
                state = pickerState,
                title = {
                    Text(
                        text = stringResource(R.string.dashboard_choose_date_range),
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                    )
                },
                headline = {
                    Text(
                        text = buildDashboardDateRangeHeadline(
                            startMillis = pickerState.selectedStartDateMillis,
                            endMillis = pickerState.selectedEndDateMillis,
                            formatter = compactDateFormatter
                        ),
                        modifier = Modifier.padding(horizontal = 24.dp),
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                showModeToggle = false
            )
        }
    }
}

@Composable
private fun DashboardScreenContent(
    uiState: DashboardUiState,
    onCustomClick: () -> Unit,
    onMonthClick: () -> Unit,
    onYearClick: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onValueClick: () -> Unit,
    onIncomeClick: () -> Unit,
    onExpenseClick: () -> Unit
) {
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
                text = stringResource(R.string.dashboard_title),
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Black
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = uiState.period == DatePeriod.CUSTOM,
                    onClick = onCustomClick,
                    label = { Text(stringResource(R.string.dashboard_period_custom)) }
                )
                FilterChip(
                    selected = uiState.period == DatePeriod.MONTH,
                    onClick = onMonthClick,
                    label = { Text(stringResource(R.string.dashboard_period_month)) }
                )
                FilterChip(
                    selected = uiState.period == DatePeriod.YEAR,
                    onClick = onYearClick,
                    label = { Text(stringResource(R.string.dashboard_period_year)) }
                )
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = Color.White,
                shadowElevation = 1.dp
            ) {
                when (uiState.period) {
                    DatePeriod.CUSTOM -> {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = onValueClick)
                                .padding(horizontal = 16.dp, vertical = 16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = stringResource(R.string.dashboard_choose_date_range),
                                tint = AccentBlue,
                                modifier = Modifier.padding(end = 10.dp)
                            )
                            Text(
                                text = uiState.periodLabel,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Black
                            )
                        }
                    }

                    DatePeriod.MONTH,
                    DatePeriod.YEAR -> {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = onPrevious) {
                                Icon(
                                    imageVector = Icons.Default.ChevronLeft,
                                    contentDescription = stringResource(R.string.dashboard_previous_period),
                                    tint = Color.Black
                                )
                            }

                            Text(
                                text = uiState.periodLabel,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Black,
                                modifier = Modifier.clickable(onClick = onValueClick)
                            )

                            IconButton(onClick = onNext) {
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = stringResource(R.string.dashboard_next_period),
                                    tint = Color.Black
                                )
                            }
                        }
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard(
                    title = stringResource(R.string.dashboard_total_income),
                    value = currencyFormatter.format(uiState.summary.totalIncome),
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.weight(1f),
                    onClick = onIncomeClick
                )
                StatCard(
                    title = stringResource(R.string.dashboard_total_expense),
                    value = currencyFormatter.format(uiState.summary.totalExpense),
                    color = Color(0xFFF44336),
                    modifier = Modifier.weight(1f),
                    onClick = onExpenseClick
                )
            }

            Text(
                text = stringResource(R.string.dashboard_expense_by_category),
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
                        Text(stringResource(R.string.dashboard_no_data))
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
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Surface(
        modifier = modifier.then(
            if (onClick != null) {
                Modifier.clickable(onClick = onClick)
            } else {
                Modifier
            }
        ),
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
private val AccentBlue = Color(0xFF0F5697)
private val ChartColors = listOf(
    Color(0xFF4FA6A9),
    Color(0xFF5D9B55),
    Color(0xFF8069C9),
    Color(0xFF2B73B7),
    Color(0xFFE54535),
    Color(0xFFD39A2B)
)

@Composable
private fun YearPickerDialog(
    initialYear: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var selectedYear by remember(initialYear) { mutableIntStateOf(initialYear) }

    DashboardPickerDialog(
        title = stringResource(R.string.dashboard_choose_year),
        onDismiss = onDismiss,
        onConfirm = { onConfirm(selectedYear) }
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            NumberPickerView(
                minValue = 2000,
                maxValue = 2100,
                initialValue = initialYear,
                formatter = { it.toString() },
                onValueChange = { selectedYear = it },
                modifier = Modifier.width(140.dp)
            )
        }
    }
}

@Composable
private fun DashboardPickerDialog(
    title: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.width(320.dp),
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            shadowElevation = 12.dp
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 22.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.Black
                )
                content()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.close))
                    }
                    TextButton(onClick = onConfirm) {
                        Text(stringResource(R.string.dashboard_apply))
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthYearPickerDialog(
    initialMonth: Int,
    initialYear: Int,
    onDismiss: () -> Unit,
    onConfirm: (month: Int, year: Int) -> Unit
) {
    var selectedMonth by remember(initialMonth) { mutableIntStateOf(initialMonth) }
    var selectedYear by remember(initialYear) { mutableIntStateOf(initialYear) }
    val monthFormatter: (Int) -> String = { value ->
        String.format(Locale.forLanguageTag("vi-VN"), "%02d", value)
    }

    DashboardPickerDialog(
        title = stringResource(R.string.dashboard_choose_month),
        onDismiss = onDismiss,
        onConfirm = { onConfirm(selectedMonth, selectedYear) }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.dashboard_period_month),
                    style = MaterialTheme.typography.labelMedium,
                    color = MutedText,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                NumberPickerView(
                    minValue = 1,
                    maxValue = 12,
                    initialValue = initialMonth,
                    formatter = monthFormatter,
                    onValueChange = { selectedMonth = it },
                    modifier = Modifier.width(110.dp)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.dashboard_period_year),
                    style = MaterialTheme.typography.labelMedium,
                    color = MutedText,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                NumberPickerView(
                    minValue = 2000,
                    maxValue = 2100,
                    initialValue = initialYear,
                    formatter = { it.toString() },
                    onValueChange = { selectedYear = it },
                    modifier = Modifier.width(120.dp)
                )
            }
        }
    }
}

private fun buildDashboardDateRangeHeadline(
    startMillis: Long?,
    endMillis: Long?,
    formatter: SimpleDateFormat
): String {
    val startText = startMillis?.let(formatter::format)
        ?: "--/--/----"
    val endText = endMillis?.let(formatter::format)
        ?: "--/--/----"
    return "$startText - $endText"
}

@Composable
private fun NumberPickerView(
    minValue: Int,
    maxValue: Int,
    initialValue: Int,
    formatter: (Int) -> String,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            NumberPicker(context).apply {
                this.minValue = minValue
                this.maxValue = maxValue
                wrapSelectorWheel = false
                descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
                textAlignment = android.view.View.TEXT_ALIGNMENT_CENTER
                setFormatter { formatter(it) }
                value = initialValue.coerceIn(minValue, maxValue)
                setOnValueChangedListener { _, _, newVal ->
                    onValueChange(newVal)
                }
            }
        },
        update = { picker ->
            picker.minValue = minValue
            picker.maxValue = maxValue
            picker.value = initialValue.coerceIn(minValue, maxValue)
            picker.setFormatter { formatter(it) }
        }
    )
}

private fun Long.toLocalDate(): LocalDate {
    return Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun DashboardScreenPreview() {
    BudgetControl_JetpackTheme(dynamicColor = false) {
        DashboardScreenContent(
            uiState = DashboardUiState(
                period = DatePeriod.MONTH,
                periodLabel = "04/2026",
                summary = DashboardSummary(32000000.0, 12500000.0, 19500000.0),
                stats = listOf(
                    CategoryExpenseStat(1, "Ăn uống", 3200000.0, 0.3f),
                    CategoryExpenseStat(2, "Đi lại", 1800000.0, 0.18f),
                    CategoryExpenseStat(3, "Giải trí", 1500000.0, 0.15f)
                )
            ),
            onCustomClick = {},
            onMonthClick = {},
            onYearClick = {},
            onPrevious = {},
            onNext = {},
            onValueClick = {},
            onIncomeClick = {},
            onExpenseClick = {}
        )
    }
}
