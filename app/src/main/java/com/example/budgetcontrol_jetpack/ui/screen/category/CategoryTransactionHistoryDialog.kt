package com.example.budgetcontrol_jetpack.ui.screen.category

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.budgetcontrol_jetpack.R
import com.example.budgetcontrol_jetpack.ui.theme.BudgetControl_JetpackTheme
import com.example.clean.entities.Category
import com.example.clean.entities.CategoryType
import com.example.clean.entities.Transaction
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun CategoryTransactionHistoryDialog(
    category: Category,
    transactions: List<Transaction>,
    onDismiss: () -> Unit
) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"))
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy\nHH:mm:ss", Locale.forLanguageTag("vi-VN"))

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFFFBF5FD),
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = stringResource(R.string.category_transaction_history_title, category.name),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF333039)
                )

                if (transactions.isEmpty()) {
                    Text(
                        text = stringResource(R.string.transaction_empty),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF77737D)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 280.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(transactions, key = { it.id }) { transaction ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = currencyFormatter.format(transaction.amount),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color(0xFFE53935)
                                    )
                                    Text(
                                        text = transaction.note.ifBlank { transaction.title },
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFF514B57)
                                    )
                                }

                                Text(
                                    text = dateFormatter.format(transaction.createdAt),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF77737D)
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.close))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 740)
@Composable
private fun CategoryTransactionHistoryDialogPreview() {
    BudgetControl_JetpackTheme(dynamicColor = false) {
        CategoryTransactionHistoryDialog(
            category = Category(
                id = 1,
                name = "Ăn uống",
                type = CategoryType.EXPENSE,
                isDefault = false
            ),
            transactions = listOf(
                Transaction(
                    id = 1,
                    title = "Ăn uống",
                    amount = 120000.0,
                    type = com.example.clean.entities.TransactionType.EXPENSE,
                    categoryId = 1,
                    note = "Bữa trưa",
                    createdAt = System.currentTimeMillis()
                )
            ),
            onDismiss = {}
        )
    }
}
