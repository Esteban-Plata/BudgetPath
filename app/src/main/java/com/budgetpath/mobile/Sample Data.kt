package com.budgetpath.mobile

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalGasStation
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.ui.graphics.Color

// fake data for now, we can replace this later with real data
// Transaction is defined in Dashboard.kt (name, date, amount, icon, iconColor, tileColor)

val sampleTransactions = listOf(
    Transaction("Sephora", "Today, 2:14 PM", 42.50, Icons.Outlined.ShoppingBag, Color(0xFFF59E0B), Color(0xFFFFF4DC)),
    Transaction("Panda Express", "Tuesday, 3:45 PM", 12.76, Icons.Outlined.Restaurant, Color(0xFFE11D48), Color(0xFFFFE4E9)),
    Transaction("Chevron", "Monday, 5:14 PM", 12.76, Icons.Outlined.LocalGasStation, Color(0xFF1565C0), Color(0xFFE3EEFB))
)

const val sampleRemaining = 209.95
const val sampleTotal = 972.00
const val sampleDaysLeft = 2

// ---- fake numbers for the Statistics screen ----
const val sampleLifetimeSavings = 1896.50
const val sampleComparedToLast = 15.83
const val sampleAvgSaved = 42.50
const val sampleAvgSpent = 42.50
const val sampleAvgOverspent = 15.83
const val sampleTimesOverBudget = 3
const val sampleTopCategory = "Food"
const val sampleTopMerchant = "Panda Express"
const val sampleBestPeriod = "Nov 1 - Nov 14"