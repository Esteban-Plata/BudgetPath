package com.budgetpath.mobile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.budgetpath.mobile.ui.theme.CardBorder
import com.budgetpath.mobile.ui.theme.LinkBlue
import com.budgetpath.mobile.ui.theme.NavyCard
import com.budgetpath.mobile.ui.theme.PillBackground
import com.budgetpath.mobile.ui.theme.PillText
import com.budgetpath.mobile.ui.theme.ProgressFill
import com.budgetpath.mobile.ui.theme.ProgressTrack
import com.budgetpath.mobile.ui.theme.ScreenBackground
import java.util.Locale

// one transaction (the two colors are for the little icon square)
data class Transaction(
    val name: String,
    val date: String,
    val amount: Double,
    val icon: ImageVector,
    val iconColor: Color = Color(0xFF475569),
    val tileColor: Color = Color(0xFFE8ECF1)
)

@Composable
fun Dashboard() {
    // these change when the user adds a transaction
    var remaining by remember { mutableDoubleStateOf(sampleRemaining) }
    var total by remember { mutableDoubleStateOf(sampleTotal) }
    var showBudgetBox by remember { mutableStateOf(false) }
    val transactions = remember { mutableStateListOf<Transaction>().apply { addAll(sampleTransactions) } }
    var showTransaction by remember { mutableStateOf(false) }

    // show the form when the button is pressed
    if (showTransaction) {
        NewTransactionDialog(
            onCancel = { showTransaction = false },
            onCreate = { newTransaction ->
                transactions.add(0, newTransaction)
                remaining -= newTransaction.amount
                showTransaction = false
            }
        )
    }

    // show the edit budget form
    if (showBudgetBox) {
        EditBudget(
            currentBudget = total,
            onCancel = { showBudgetBox = false },
            onSave = { newTotal ->
                // keep the same amount spent, so remaining moves by the difference
                remaining += (newTotal - total)
                total = newTotal
                showBudgetBox = false
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // the budget card at the top
        item {
            BudgetCard(
                remaining = remaining,
                total = total,
                daysLeft = sampleDaysLeft,
                onAddClick = { showTransaction = true },
                onEditClick = { showBudgetBox = true }
            )
        }

        // thin line under the card
        item {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                color = CardBorder
            )
        }

        // title row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Recent Transactions", fontSize = 24.sp, fontWeight = FontWeight.Medium)
                TextButton(onClick = { }) {
                    Text("See All", color = LinkBlue, fontWeight = FontWeight.Bold)
                }
            }
        }

        // one card for each transaction
        items(transactions) { transaction ->
            TransactionCard(transaction)
        }
    }
}

@Composable
fun BudgetCard(
    remaining: Double,
    total: Double,
    daysLeft: Int,
    onAddClick: () -> Unit,
    onEditClick: () -> Unit
) {
    // how much of the budget is left (0.0 to 1.0)
    var percentLeft = (remaining / total).toFloat()
    if (percentLeft < 0f) {
        percentLeft = 0f
    }
    if (percentLeft > 1f) {
        percentLeft = 1f
    }

    // turn the numbers into text with 2 decimals
    val remainingText = String.format(Locale.US, "%.2f", remaining)
    val totalText = String.format(Locale.US, "%.2f", total)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .background(
                NavyCard,
                RoundedCornerShape(
                    topStart = 56.dp,
                    topEnd = 56.dp,
                    bottomStart = 72.dp,
                    bottomEnd = 72.dp
                )
            )
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Take-Home Budget:", color = Color.White, fontSize = 24.sp)

        Spacer(modifier = Modifier.height(16.dp))

        // Available is Large. smaller is total
        Row {
            Text(
                text = "\$$remainingText /",
                color = Color.White,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.alignByBaseline()
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "\$$totalText total",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.alignByBaseline()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Remaining:", color = Color.White, fontSize = 14.sp)
            Text("$daysLeft Days Left", color = Color.White, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(6.dp))

        // progress bar: gray is the base. cyan tracks usage
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(CircleShape)
                .background(ProgressTrack)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(percentLeft)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(ProgressFill)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("0%", color = Color.White, fontSize = 14.sp)
            Text("100%", color = Color.White, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
            PillButton(
                text = "Add\nTransaction",
                icon = Icons.Outlined.CreditCard,
                onClick = onAddClick
            )
            PillButton(
                text = "Edit\nBudget",
                icon = Icons.Outlined.AttachMoney,
                onClick = onEditClick
            )
        }
    }
}

// For buttons on the budget box
@Composable
fun PillButton(text: String, icon: ImageVector, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PillBackground,
            contentColor = PillText
        )
    ) {
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun TransactionCard(transaction: Transaction) {
    val amountText = String.format(Locale.US, "%.2f", transaction.amount)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // colored square with the icon inside
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(transaction.tileColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    transaction.icon,
                    contentDescription = null,
                    tint = transaction.iconColor
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 14.dp)
            ) {
                Text(transaction.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(transaction.date, color = Color.Gray, fontSize = 13.sp)
            }

            Text(
                text = "-\$$amountText",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    Dashboard()
}