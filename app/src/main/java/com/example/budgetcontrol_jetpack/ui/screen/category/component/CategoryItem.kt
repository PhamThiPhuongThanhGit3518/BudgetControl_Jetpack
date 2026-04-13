package com.example.budgetcontrol_jetpack.ui.screen.category.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.clean.entities.Category

@Composable
fun CategoryItem(
    category: Category,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(parseColor(category.colorHex))
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = category.type.name,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Icon: ${category.icon}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                OutlinedButton(onClick = onEditClick) {
                    Text("Sửa")
                }

                TextButton(
                    onClick = onDeleteClick,
                    enabled = !category.isDefault
                ) {
                    Text(if (category.isDefault) "Mặc định" else "Xóa")
                }
            }
        }
    }
}

private fun parseColor(hex: String): Color {
    return runCatching {
        Color(android.graphics.Color.parseColor(hex))
    }.getOrDefault(Color.Gray)
}