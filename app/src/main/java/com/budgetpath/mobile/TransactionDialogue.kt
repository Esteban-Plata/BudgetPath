package com.budgetpath.mobile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.LocalGasStation
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview

// Category Dropdown Options
val categories = listOf("Bills", "Food", "Gas", "Shopping")

// Select Relevant Icon
fun getIcon(category: String): ImageVector {
    return when (category) {
        "Food" -> Icons.Outlined.Restaurant
        "Gas" -> Icons.Outlined.LocalGasStation
        "Shopping" -> Icons.Outlined.ShoppingBag
        else -> Icons.Outlined.AttachMoney
    }
}

//Composable start
@Composable
fun NewTransactionDialog(
    onCancel: () -> Unit,
    onCreate: (Transaction) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Bills") }
    var merchant by remember { mutableStateOf("") }
    var menuOpen by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("New Transaction") },
        text = {
            Column {
                Text("Transaction Name")
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Amount")
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    placeholder = { Text("\$XX.xx") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Category")
                OutlinedButton(
                    onClick = { menuOpen = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(category)
                }
                DropdownMenu(
                    expanded = menuOpen,
                    onDismissRequest = { menuOpen = false }
                ) {
                    for (option in categories) {
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                category = option
                                menuOpen = false
                            }
                        )
                    }
                }

                Text("Merchant")
                OutlinedTextField(
                    value = merchant,
                    onValueChange = { merchant = it },
                    placeholder = { Text("Merchant name") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val amountNumber = amount.toDoubleOrNull()

                // only create it if the name and amount are filled in
                if (name.isNotBlank() && amountNumber != null) {
                    val newTransaction = Transaction(
                        name = if (merchant.isNotBlank()) merchant else name,
                        date = "Today",
                        amount = amountNumber,
                        icon = getIcon(category)
                    )
                    onCreate(newTransaction)
                }
            }) {
                Text("Create Transaction")
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
fun AddTransactionView() {
    NewTransactionDialog(
        onCancel = { },
        onCreate = { }
    )
}