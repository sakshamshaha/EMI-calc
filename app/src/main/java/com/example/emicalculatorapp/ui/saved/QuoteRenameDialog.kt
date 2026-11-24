package com.example.emicalculatorapp.ui.saved

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.example.emicalculatorapp.model.LoanCategory
import com.example.emicalculatorapp.ui.theme.SpaceGroteskFamily

@Composable
fun QuoteRenameDialog(
    initialName: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var renameInput by remember { mutableStateOf(initialName) }
    val isValid = renameInput.trim().isNotEmpty() && renameInput.trim().length <= 40

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Rename Loan Quote",
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = renameInput,
                    onValueChange = { if (it.length <= 40) renameInput = it },
                    label = { Text("Quote Name") },
                    supportingText = { Text("${renameInput.length}/40") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (isValid) {
                        onConfirm(renameInput.trim())
                    }
                },
                enabled = isValid
            ) {
                Text("Rename", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

fun getCategoryIcon(category: LoanCategory): ImageVector = when (category) {
    LoanCategory.HOME -> Icons.Default.Home
    LoanCategory.CAR -> Icons.Default.DirectionsCar
    LoanCategory.PERSONAL -> Icons.Default.Work
    LoanCategory.EDUCATION -> Icons.Default.School
}
