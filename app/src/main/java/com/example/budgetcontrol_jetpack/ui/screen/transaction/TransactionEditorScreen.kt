package com.example.budgetcontrol_jetpack.ui.screen.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.budgetcontrol_jetpack.R
import com.example.budgetcontrol_jetpack.ui.theme.BudgetControl_JetpackTheme
import com.example.budgetcontrol_jetpack.viewmodel.transaction.TransactionEditorViewModel
import com.example.budgetcontrol_jetpack.viewmodel.transaction.TransactionEditorUiState
import com.example.clean.entities.Category
import com.example.clean.entities.CategoryType
import com.example.clean.entities.TransactionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionEditorScreen(
    viewModel: TransactionEditorViewModel,
    onDone: () -> Unit,
    onDismiss: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    TransactionEditorContent(
        uiState = uiState,
        updateType = viewModel::updateType,
        updateCategory = viewModel::updateCategory,
        updateAmount = viewModel::updateAmount,
        updateNote = viewModel::updateNote,
        onSave = { viewModel.save(onDone) },
        onDismiss = onDismiss
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransactionEditorContent(
    uiState: TransactionEditorUiState,
    updateType: (TransactionType) -> Unit,
    updateCategory: (Long) -> Unit,
    updateAmount: (String) -> Unit,
    updateNote: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    var isCategoryExpanded by remember { mutableStateOf(false) }
    val selectedCategory = uiState.categories.firstOrNull { it.id == uiState.categoryId }
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White,
        focusedLabelColor = FieldLabel,
        unfocusedLabelColor = FieldLabel,
        focusedPlaceholderColor = FieldLabel,
        unfocusedPlaceholderColor = FieldLabel,
        focusedBorderColor = FieldBorder,
        unfocusedBorderColor = FieldBorder,
        cursorColor = Color.Black
    )
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        containerColor = SheetBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 22.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = Color(0xFF333039)
                    )
                }

                Text(
                    text = stringResource(
                        if (uiState.id > 0) {
                            R.string.transaction_edit_title
                        } else {
                            R.string.transaction_add
                        }
                    ),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF333039),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.size(0.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TypeBackground, RoundedCornerShape(12.dp))
                    .padding(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TransactionTypeToggle(
                        text = stringResource(R.string.type_income_short),
                        selected = uiState.type == TransactionType.INCOME,
                        onClick = { updateType(TransactionType.INCOME) },
                        modifier = Modifier.weight(1f)
                    )
                    TransactionTypeToggle(
                        text = stringResource(R.string.type_expense_short),
                        selected = uiState.type == TransactionType.EXPENSE,
                        onClick = { updateType(TransactionType.EXPENSE) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            ExposedDropdownMenuBox(
                expanded = isCategoryExpanded,
                onExpandedChange = {
                    isCategoryExpanded = uiState.categories.isNotEmpty() && !isCategoryExpanded
                }
            ) {
                OutlinedTextField(
                    value = selectedCategory?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.transaction_category_label)) },
                    placeholder = { Text(stringResource(R.string.transaction_category_placeholder)) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryExpanded)
                    },
                    colors = fieldColors,
                    modifier = Modifier
                        .menuAnchor(
                            type = MenuAnchorType.PrimaryNotEditable,
                            enabled = uiState.categories.isNotEmpty()
                        )
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = isCategoryExpanded,
                    onDismissRequest = { isCategoryExpanded = false },
                    containerColor = Color.White
                ) {
                    uiState.categories.forEach { category ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = category.name,
                                    color = Color.Black
                                )
                            },
                            onClick = {
                                updateCategory(category.id)
                                isCategoryExpanded = false
                            },
                            colors = MenuDefaults.itemColors(
                                textColor = Color.Black
                            )
                        )
                    }
                }
            }

            OutlinedTextField(
                value = uiState.amount,
                onValueChange = updateAmount,
                label = { Text(stringResource(R.string.transaction_amount_label)) },
                placeholder = { Text(stringResource(R.string.transaction_amount_placeholder)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                colors = fieldColors,
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.note,
                onValueChange = updateNote,
                label = { Text(stringResource(R.string.transaction_note_label)) },
                colors = fieldColors,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            )

            if (!uiState.errorMessage.isNullOrBlank()) {
                Text(
                    text = uiState.errorMessage.orEmpty(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E91EC),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(R.string.transaction_save))
            }
        }
    }
}

@Composable
private fun TransactionTypeToggle(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) Color.White else Color.Transparent,
            contentColor = if (selected) SelectedTypeText else UnselectedTypeText
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            focusedElevation = 0.dp,
            hoveredElevation = 0.dp,
            disabledElevation = 0.dp
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}

private val SheetBackground = Color(0xFFFBF5FD)
private val FieldBorder = Color(0xFF8E8794)
private val FieldLabel = Color(0xFF6F6874)
private val TypeBackground = Color(0xFFE6ECF2)
private val SelectedTypeText = Color(0xFFE63B61)
private val UnselectedTypeText = Color(0xFF6C7584)

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun TransactionEditorScreenPreview() {
    BudgetControl_JetpackTheme(dynamicColor = false) {
        TransactionEditorContent(
            uiState = TransactionEditorUiState(
                amount = "1.250.000",
                type = TransactionType.EXPENSE,
                categoryId = 1,
                categories = listOf(
                    Category(1, "Ăn uống", CategoryType.EXPENSE, false),
                    Category(2, "Đi lại", CategoryType.EXPENSE, false)
                ),
                note = "Bữa tối"
            ),
            updateType = {},
            updateCategory = {},
            updateAmount = {},
            updateNote = {},
            onSave = {},
            onDismiss = {}
        )
    }
}
