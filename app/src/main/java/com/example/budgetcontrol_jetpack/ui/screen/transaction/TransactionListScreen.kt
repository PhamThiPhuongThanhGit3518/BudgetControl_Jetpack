package com.example.budgetcontrol_jetpack.ui.screen.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import com.example.budgetcontrol_jetpack.R
import com.example.budgetcontrol_jetpack.ui.theme.BudgetControl_JetpackTheme
import com.example.budgetcontrol_jetpack.viewmodel.transaction.TransactionListUiState
import com.example.budgetcontrol_jetpack.viewmodel.transaction.TransactionListViewModel
import com.example.clean.entities.Transaction
import com.example.clean.entities.TransactionType
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun TransactionListScreen(
    viewModel: TransactionListViewModel,
    onAddClick: () -> Unit,
    onEditClick: (Long) -> Unit,
    onAvatarClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    TransactionListContent(
        uiState = uiState,
        onAddClick = onAddClick,
        onEditClick = onEditClick,
        onAvatarClick = onAvatarClick,
        onDeleteClick = viewModel::delete
    )
}

@Composable
private fun TransactionListContent(
    uiState: TransactionListUiState,
    onAddClick: () -> Unit,
    onEditClick: (Long) -> Unit,
    onAvatarClick: () -> Unit,
    onDeleteClick: (Transaction) -> Unit
) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"))
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("vi-VN"))
    val timeFormatter = SimpleDateFormat("HH:mm", Locale.forLanguageTag("vi-VN"))
    var expandedTransactionId by remember { mutableStateOf<Long?>(null) }
    var transactionPendingDelete by remember { mutableStateOf<Transaction?>(null) }

    Scaffold(
        containerColor = ScreenBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = AccentBlue,
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.transaction_add_content_description)
                )
            }
        }
    ) { padding ->
        if (uiState.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
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
                    HomeHeader(onAvatarClick = onAvatarClick)
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.transaction_total_balance),
                            style = MaterialTheme.typography.titleMedium,
                            color = SummaryLabelText,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = currencyFormatter.format(uiState.balance),
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 31.sp
                            ),
                            color = BalanceText
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SummaryCard(
                            title = stringResource(R.string.summary_income_title),
                            amount = currencyFormatter.format(uiState.totalIncome),
                            accentColor = IncomeText,
                            prefix = "+ ",
                            isIncome = true,
                            modifier = Modifier.weight(1f)
                        )
                        SummaryCard(
                            title = stringResource(R.string.summary_expense_title),
                            amount = currencyFormatter.format(uiState.totalExpense),
                            accentColor = ExpenseText,
                            prefix = "- ",
                            isIncome = false,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Text(
                        text = stringResource(R.string.transaction_history_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF000000),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                if (uiState.transactions.isEmpty()) {
                    item {
                        Text(
                            text = stringResource(R.string.transaction_empty),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                items(uiState.transactions, key = { it.id }) { item ->
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
                                        text = buildTransactionSubtitle(
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

                        DropdownMenu(
                            expanded = expandedTransactionId == item.id,
                            onDismissRequest = { expandedTransactionId = null }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.transaction_edit)) },
                                onClick = {
                                    expandedTransactionId = null
                                    onEditClick(item.id)
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = null,
                                        tint = AccentBlue
                                    )
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.transaction_delete)) },
                                onClick = {
                                    expandedTransactionId = null
                                    transactionPendingDelete = item
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = null,
                                        tint = ExpenseText
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }

        transactionPendingDelete?.let { transaction ->
            DeleteTransactionConfirmDialog(
                transactionTitle = buildTransactionSubtitle(
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
    }
}

@Composable
private fun HomeHeader(
    onAvatarClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onAvatarClick) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = stringResource(R.string.transaction_header_account),
                tint = Color(0xFF5FB7CF),
                modifier = Modifier.size(34.dp)
            )
        }
    }
}

@Composable
private fun SummaryCard(
    title: String,
    amount: String,
    accentColor: Color,
    prefix: String,
    isIncome: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = SummaryBorder,
                shape = RoundedCornerShape(14.dp)
            ),
        shape = RoundedCornerShape(14.dp),
        color = SummaryCardBackground,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(
                        id = if (isIncome) R.drawable.ic_up else R.drawable.ic_down
                    ),
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(32.dp)
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                ),
                color = SummaryTitleText
            )
            Text(
                text = prefix + amount,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                ),
                color = accentColor
            )
        }
    }
}

private val ScreenBackground = Color(0xFFFBF5FD)
private val AccentBlue = Color(0xFF0F5697)
private val MutedText = Color(0xFF77737D)
private val SummaryLabelText = Color(0xFF6C6871)
private val BalanceText = Color(0xFF2F8F45)
private val SummaryTitleText = Color(0xFF78A7C9)
private val SummaryBorder = Color(0xFFB3ABB2)
private val SummaryCardBackground = Color(0xFFF9F9F7)
private val IncomeSoft = Color(0xFFCFEFD3)
private val IncomeText = Color(0xFF2F8F45)
private val ExpenseSoft = Color(0xFFFFC9CF)
private val ExpenseText = Color(0xFFE53935)

private fun buildTransactionSubtitle(
    categoryName: String,
    note: String
): String {
    return if (note.isBlank()) {
        categoryName
    } else {
        "$categoryName - $note"
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun TransactionListScreenPreview() {
    BudgetControl_JetpackTheme(dynamicColor = false) {
        TransactionListContent(
            uiState = TransactionListUiState(
                isLoading = false,
                transactions = listOf(
                    Transaction(
                        id = 1,
                        title = "Ăn uống",
                        amount = 120000.0,
                        type = TransactionType.EXPENSE,
                        categoryId = 1,
                        note = "Bữa trưa",
                        createdAt = System.currentTimeMillis()
                    ),
                    Transaction(
                        id = 2,
                        title = "Lương",
                        amount = 25000000.0,
                        type = TransactionType.INCOME,
                        categoryId = 2,
                        note = "",
                        createdAt = System.currentTimeMillis()
                    )
                ),
                totalIncome = 25000000.0,
                totalExpense = 120000.0,
                balance = 24880000.0
            ),
            onAddClick = {},
            onEditClick = {},
            onAvatarClick = {},
            onDeleteClick = {}
        )
    }
}
