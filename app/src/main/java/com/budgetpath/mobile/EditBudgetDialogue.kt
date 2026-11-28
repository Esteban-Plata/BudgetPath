package com.budgetpath.mobile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import java.util.Locale

//Composable start
@Composable
fun EditBudget(
    currentBudget: Double,
    onCancel: () -> Unit,
    onSave: (Double) -> Unit
) {
    // start with the current budget filled in
    var amount by remember { mutableStateOf(String.format(Locale.US, "%.2f", currentBudget)) }

    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("Edit Budget") },
        text = {
            Column {
                Text("Amount")
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    placeholder = { Text("\$XX.xx") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val amountNumber = amount.toDoubleOrNull()

                // only save it if the amount is a number bigger than 0
                if (amountNumber != null && amountNumber > 0) {
                    onSave(amountNumber)
                }
            }) {
                Text("Save Budget")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text("Cancel")
            }
        }
    )
}
//Composable end

// Preview
@Preview(showBackground = true)
@Composable
fun EditBudgetView() {
    EditBudget(
        currentBudget = 972.00,
        onCancel = { },
        onSave = { }
    )
}