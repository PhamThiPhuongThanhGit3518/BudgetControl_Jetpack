package com.example.budgetcontrol_jetpack.ui.screen.category

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.budgetcontrol_jetpack.R
import com.example.budgetcontrol_jetpack.ui.theme.BudgetControl_JetpackTheme
import com.example.budgetcontrol_jetpack.viewmodel.category.CategoryEditorViewModel
import com.example.budgetcontrol_jetpack.viewmodel.category.CategoryEditorUiState
import com.example.clean.entities.CategoryType

@Composable
fun CategoryEditorScreen(
    viewModel: CategoryEditorViewModel,
    onDone: () -> Unit,
    onDismiss: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    CategoryEditorContent(
        uiState = uiState,
        updateName = viewModel::updateName,
        updateType = viewModel::updateType,
        onSave = { viewModel.save(onDone) },
        onDismiss = onDismiss
    )
}

@Composable
private fun CategoryEditorContent(
    uiState: CategoryEditorUiState,
    updateName: (String) -> Unit,
    updateType: (CategoryType) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
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

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            color = SheetBackground,
            shadowElevation = 12.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(HeaderBlue)
                        .padding(start = 16.dp, top = 12.dp, end = 10.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(
                            if (uiState.id == 0L) {
                                R.string.category_editor_add_title
                            } else {
                                R.string.category_editor_edit_title
                            }
                        ),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.close),
                            tint = Color.White
                        )
                    }
                }

                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = stringResource(R.string.category_name_label),
                            style = MaterialTheme.typography.titleSmall,
                            color = SectionText,
                            fontWeight = FontWeight.SemiBold
                        )
                        OutlinedTextField(
                            value = uiState.name,
                            onValueChange = updateName,
                            placeholder = {
                                Text(stringResource(R.string.category_name_placeholder))
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = fieldColors,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = stringResource(R.string.category_transaction_type_label),
                            style = MaterialTheme.typography.titleSmall,
                            color = SectionText,
                            fontWeight = FontWeight.SemiBold
                        )
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
                                CategoryTypeToggle(
                                    text = stringResource(R.string.category_type_income),
                                    selected = uiState.type == CategoryType.INCOME,
                                    onClick = { updateType(CategoryType.INCOME) },
                                    modifier = Modifier.weight(1f)
                                )
                                CategoryTypeToggle(
                                    text = stringResource(R.string.category_type_expense),
                                    selected = uiState.type == CategoryType.EXPENSE,
                                    onClick = { updateType(CategoryType.EXPENSE) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    if (!uiState.errorMessage.isNullOrBlank()) {
                        Text(
                            text = uiState.errorMessage.orEmpty(),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Button(
                        onClick = onSave,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ActionBlue,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            stringResource(
                                if (uiState.id == 0L) {
                                    R.string.category_save
                                } else {
                                    R.string.category_save_changes
                                }
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryTypeToggle(
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

private val SheetBackground = Color(0xFFEDE6F1)
private val HeaderBlue = Color(0xFF4E86BF)
private val ActionBlue = Color(0xFF3A8CE6)
private val FieldBorder = Color(0xFF6A5F68)
private val FieldLabel = Color(0xFF7D7480)
private val SectionText = Color(0xFF302B2F)
private val TypeBackground = Color(0xFFE6ECF2)
private val SelectedTypeText = Color(0xFFE63B61)
private val UnselectedTypeText = Color(0xFF6C7584)

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
private fun CategoryEditorScreenPreview() {
    BudgetControl_JetpackTheme(dynamicColor = false) {
        CategoryEditorContent(
            uiState = CategoryEditorUiState(
                name = "Ăn uống",
                type = CategoryType.INCOME
            ),
            updateName = {},
            updateType = {},
            onSave = {},
            onDismiss = {}
        )
    }
}
