package com.example.budgetcontrol_jetpack.ui.screen.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.budgetcontrol_jetpack.R
import com.example.budgetcontrol_jetpack.viewmodel.transaction.TransactionListUiState
import com.example.budgetcontrol_jetpack.viewmodel.transaction.TransactionListViewModel
import com.example.clean.entities.Transaction
import com.example.clean.entities.TransactionType
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private const val PAGE_SIZE = 20

@Composable
fun TransactionHistoryScreen(
    viewModel: TransactionListViewModel,
    onBack: () -> Unit,
    onEditClick: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    TransactionHistoryContent(
        uiState = uiState,
        onBack = onBack,
        onEditClick = onEditClick,
        onDeleteClick = viewModel::delete
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransactionHistoryContent(
    uiState: TransactionListUiState,
    onBack: () -> Unit,
    onEditClick: (Long) -> Unit,
    onDeleteClick: (Transaction) -> Unit
) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"))
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("vi-VN")) }
    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.forLanguageTag("vi-VN")) }
    var expandedTransactionId by remember { mutableStateOf<Long?>(null) }
    var transactionPendingDelete by remember { mutableStateOf<Transaction?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var startDateMillis by rememberSaveable { mutableStateOf<Long?>(null) }
    var endDateMillis by rememberSaveable { mutableStateOf<Long?>(null) }
    var visibleCount by rememberSaveable(startDateMillis, endDateMillis) { mutableIntStateOf(PAGE_SIZE) }

    val filteredTransactions = remember(uiState.transactions, startDateMillis, endDateMillis) {
        uiState.transactions
            .sortedByDescending { it.createdAt }
            .filter { transaction ->
                isTransactionInDateRange(
                    transaction = transaction,
                    startDateMillis = startDateMillis,
                    endDateMillis = endDateMillis
                )
            }
    }
    val visibleTransactions = filteredTransactions.take(visibleCount)
    val hasMore = visibleTransactions.size < filteredTransactions.size

    Scaffold(
        containerColor = ScreenBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = Color(0xFF333039)
                    )
                }
                Text(
                    text = stringResource(R.string.transaction_history_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }

            DateRangeFilterBar(
                startDateMillis = startDateMillis,
                endDateMillis = endDateMillis,
                dateFormatter = dateFormatter,
                onOpenPicker = { showDatePicker = true },
                onClear = {
                    startDateMillis = null
                    endDateMillis = null
                    visibleCount = PAGE_SIZE
                }
            )

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (filteredTransactions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 24.dp),
                    contentAlignment = Alignment.TopStart
                ) {
                    Text(
                        text = stringResource(R.string.transaction_empty),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(visibleTransactions, key = { _, item -> item.id }) { index, item ->
                        if (index == visibleTransactions.lastIndex && hasMore) {
                            LaunchedEffect(visibleTransactions.size, startDateMillis, endDateMillis) {
                                visibleCount = (visibleCount + PAGE_SIZE).coerceAtMost(filteredTransactions.size)
                            }
                        }

                        Box {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { expandedTransactionId = item.id },
                                shape = MaterialTheme.shapes.medium,
                                color = Color.White,
                                shadowElevation = 1.dp
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val isIncome = item.type == TransactionType.INCOME
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .background(
                                                color = if (isIncome) IncomeSoft else ExpenseSoft,
                                                shape = CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (isIncome) {
                                                Icons.Default.ArrowUpward
                                            } else {
                                                Icons.Default.ArrowDownward
                                            },
                                            contentDescription = null,
                                            tint = if (isIncome) IncomeText else ExpenseText
                                        )
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = currencyFormatter.format(item.amount),
                                            style = MaterialTheme.typography.titleSmall,
                                            color = if (isIncome) IncomeText else ExpenseText
                                        )
                                        Text(
                                            text = buildHistoryTransactionSubtitle(
                                                categoryName = item.title,
                                                note = item.note
                                            ),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF4F4A55)
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = dateFormatter.format(item.createdAt),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MutedText
                                        )
                                        Text(
                                            text = timeFormatter.format(item.createdAt),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MutedText
                                        )
                                    }
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(end = 16.dp, bottom = 8.dp)
                            ) {
                                MaterialTheme(
                                    colorScheme = MaterialTheme.colorScheme.copy(surface = Color.White)
                                ) {
                                    DropdownMenu(
                                        expanded = expandedTransactionId == item.id,
                                        onDismissRequest = { expandedTransactionId = null },
                                        offset = DpOffset(x = 0.dp, y = 4.dp),
                                        modifier = Modifier.background(Color.White)
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text(stringResource(R.string.transaction_edit)) },
                                            onClick = {
                                                expandedTransactionId = null
                                                onEditClick(item.id)
                                            },
                                            leadingIcon = {
                                                Icon(Icons.Default.Edit, null, tint = AccentBlue)
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text(stringResource(R.string.transaction_delete)) },
                                            onClick = {
                                                expandedTransactionId = null
                                                transactionPendingDelete = item
                                            },
                                            leadingIcon = {
                                                Icon(Icons.Default.Delete, null, tint = ExpenseText)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (hasMore) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp
                                )
                                Text(
                                    text = stringResource(R.string.transaction_history_load_more),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MutedText,
                                    modifier = Modifier.padding(start = 10.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        transactionPendingDelete?.let { transaction ->
            DeleteTransactionConfirmDialog(
                transactionTitle = buildHistoryTransactionSubtitle(
                    categoryName = transaction.title,
                    note = transaction.note
                ),
                onDismiss = { transactionPendingDelete = null },
                onConfirm = {
                    onDeleteClick(transaction)
                    transactionPendingDelete = null
                }
            )
        }

        if (showDatePicker) {
            val pickerState = rememberDateRangePickerState(
                initialSelectedStartDateMillis = startDateMillis,
                initialSelectedEndDateMillis = endDateMillis
            )

            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    Button(
                        onClick = {
                            startDateMillis = pickerState.selectedStartDateMillis?.startOfDayMillis()
                            endDateMillis = pickerState.selectedEndDateMillis?.endOfDayMillis()
                            visibleCount = PAGE_SIZE
                            showDatePicker = false
                        },
                        enabled = pickerState.selectedStartDateMillis != null &&
                            pickerState.selectedEndDateMillis != null
                    ) {
                        Text(stringResource(R.string.transaction_history_apply_date_range))
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showDatePicker = false }) {
                        Text(stringResource(R.string.close))
                    }
                }
            ) {
                DateRangePicker(
                    state = pickerState,
                    title = {
                        Text(
                            text = stringResource(R.string.transaction_history_date_range_title),
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                        )
                    },
                    headline = {
                        Text(
                            text = buildSelectedDateRangeLabel(
                                startDateMillis = pickerState.selectedStartDateMillis,
                                endDateMillis = pickerState.selectedEndDateMillis,
                                dateFormatter = dateFormatter
                            ),
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    },
                    showModeToggle = false
                )
            }
        }
    }
}

@Composable
private fun DateRangeFilterBar(
    startDateMillis: Long?,
    endDateMillis: Long?,
    dateFormatter: SimpleDateFormat,
    onOpenPicker: () -> Unit,
    onClear: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(AccentBlue.copy(alpha = 0.12f), CircleShape)
                    .clickable(onClick = onOpenPicker),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = stringResource(R.string.transaction_history_choose_date_range),
                    tint = AccentBlue
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onOpenPicker)
            ) {
                Text(
                    text = stringResource(R.string.transaction_history_choose_date_range),
                    style = MaterialTheme.typography.labelMedium,
                    color = MutedText
                )
                Text(
                    text = buildAppliedDateRangeLabel(startDateMillis, endDateMillis, dateFormatter),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium
                )
            }

            if (startDateMillis != null || endDateMillis != null) {
                Text(
                    text = stringResource(R.string.transaction_history_clear_date_range),
                    style = MaterialTheme.typography.labelMedium,
                    color = AccentBlue,
                    modifier = Modifier.clickable(onClick = onClear)
                )
            }
        }
    }
}

private fun isTransactionInDateRange(
    transaction: Transaction,
    startDateMillis: Long?,
    endDateMillis: Long?
): Boolean {
    val isAfterStart = startDateMillis == null || transaction.createdAt >= startDateMillis
    val isBeforeEnd = endDateMillis == null || transaction.createdAt <= endDateMillis
    return isAfterStart && isBeforeEnd
}

private fun buildAppliedDateRangeLabel(
    startDateMillis: Long?,
    endDateMillis: Long?,
    dateFormatter: SimpleDateFormat
): String {
    if (startDateMillis == null || endDateMillis == null) {
        return "Tất cả thời gian"
    }

    return "${dateFormatter.format(startDateMillis)} - ${dateFormatter.format(endDateMillis)}"
}

private fun buildSelectedDateRangeLabel(
    startDateMillis: Long?,
    endDateMillis: Long?,
    dateFormatter: SimpleDateFormat
): String {
    val startLabel = startDateMillis?.let(dateFormatter::format)
        ?: "Từ ngày"
    val endLabel = endDateMillis?.let(dateFormatter::format)
        ?: "Đến ngày"
    return "$startLabel - $endLabel"
}

private fun Long.startOfDayMillis(): Long {
    return Calendar.getInstance().run {
        timeInMillis = this@startOfDayMillis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        timeInMillis
    }
}

private fun Long.endOfDayMillis(): Long {
    return Calendar.getInstance().run {
        timeInMillis = this@endOfDayMillis
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
        timeInMillis
    }
}

private fun buildHistoryTransactionSubtitle(
    categoryName: String,
    note: String
): String {
    return if (note.isBlank()) {
        categoryName
    } else {
        "$categoryName - $note"
    }
}

private val ScreenBackground = Color(0xFFFBF5FD)
private val AccentBlue = Color(0xFF0F5697)
private val MutedText = Color(0xFF77737D)
private val IncomeSoft = Color(0xFFCFEFD3)
private val IncomeText = Color(0xFF2F8F45)
private val ExpenseSoft = Color(0xFFFFC9CF)
private val ExpenseText = Color(0xFFE53935)
