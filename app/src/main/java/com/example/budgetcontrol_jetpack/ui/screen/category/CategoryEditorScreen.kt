package com.example.budgetcontrol_jetpack.ui.screen.category

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.budgetcontrol_jetpack.R
import com.example.budgetcontrol_jetpack.viewmodel.category.CategoryEditorViewModel
import com.example.clean.entities.CategoryType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryEditorScreen(
    viewModel: CategoryEditorViewModel,
    onDone: () -> Unit,
    onDismiss: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
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
        androidx.compose.material3.Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = SheetBackground,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = stringResource(
                        if (uiState.id == 0L) {
                            R.string.category_editor_add_title
                        } else {
                            R.string.category_editor_edit_title
                        }
                    ),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF333039)
                )

                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = viewModel::updateName,
                    label = { Text(stringResource(R.string.category_name_label)) },
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = uiState.type == CategoryType.EXPENSE,
                        onClick = { viewModel.updateType(CategoryType.EXPENSE) },
                        label = { Text(stringResource(R.string.type_expense_short)) }
                    )
                    FilterChip(
                        selected = uiState.type == CategoryType.INCOME,
                        onClick = { viewModel.updateType(CategoryType.INCOME) },
                        label = { Text(stringResource(R.string.type_income_short)) }
                    )
                }

                OutlinedTextField(
                    value = uiState.colorHex,
                    onValueChange = viewModel::updateColor,
                    label = { Text(stringResource(R.string.category_color_hex_label)) },
                    placeholder = { Text(stringResource(R.string.category_color_hex_placeholder)) },
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = uiState.icon,
                    onValueChange = viewModel::updateIcon,
                    label = { Text(stringResource(R.string.category_icon_name_label)) },
                    placeholder = { Text(stringResource(R.string.category_icon_name_placeholder)) },
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth()
                )

                if (!uiState.errorMessage.isNullOrBlank()) {
                    Text(
                        text = uiState.errorMessage.orEmpty(),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Button(
                    onClick = { viewModel.save(onDone) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0F5697),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        stringResource(
                            if (uiState.id == 0L) {
                                R.string.category_editor_add_title
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

private val SheetBackground = Color(0xFFFBF5FD)
private val FieldBorder = Color(0xFF8E8794)
private val FieldLabel = Color(0xFF6F6874)
