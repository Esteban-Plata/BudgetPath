package com.budgetpath.mobile

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoneyOff
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material.icons.outlined.TrendingDown
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.budgetpath.mobile.ui.theme.CardBorder
import com.budgetpath.mobile.ui.theme.NavyCard
import com.budgetpath.mobile.ui.theme.ScreenBackground
import java.util.Locale

// colors used on this screen
val StatGreen = Color(0xFF2E7D32)
val StatRed = Color(0xFFD50000)
val StatOrange = Color(0xFFFF8A1F)
val StatPurple = Color(0xFF4F46E5)
val StatTeal = Color(0xFF1FC8C8)

// turns 42.5 into "42.50"
fun twoDecimals(number: Double): String {
    return String.format(Locale.US, "%.2f", number)
}

@Composable
fun Statistics() {
    val savingsText = String.format(Locale.US, "%,.2f", sampleLifetimeSavings)

    // keeps track of the scroll position
    val listState = rememberLazyListState()

    // becomes true the first time the user scrolls down (and stays true)
    var userScrolled by remember { mutableStateOf(false) }
    LaunchedEffect(listState.canScrollBackward) {
        if (listState.canScrollBackward) {
            userScrolled = true
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // the dark card at the top
        item {
            SavingsCard(savingsText)
        }

        item {
            Text(
                text = "Metrics",
                fontSize = 36.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // each card fades in when it scrolls onto the screen
        item {
            FadeIn {
                MetricRow(
                    label = "Compared to Last",
                    value = "+ \$" + twoDecimals(sampleComparedToLast),
                    valueColor = StatRed,
                    icon = Icons.Outlined.TrendingDown,
                    iconColor = StatRed
                )
            }
        }
        item {
            FadeIn {
                MetricRow(
                    label = "Avg Saved per Period:",
                    value = "\$" + twoDecimals(sampleAvgSaved),
                    valueColor = StatGreen,
                    icon = Icons.Outlined.TrendingUp,
                    iconColor = StatGreen
                )
            }
        }
        item {
            FadeIn {
                MetricRow(
                    label = "Avg Spent per Period:",
                    value = "\$" + twoDecimals(sampleAvgSpent),
                    valueColor = StatRed,
                    icon = Icons.Outlined.TrendingDown,
                    iconColor = StatRed
                )
            }
        }
        item {
            FadeIn {
                MetricRow(
                    label = "Category Most Spent on:",
                    value = sampleTopCategory,
                    valueColor = StatOrange,
                    valueIcon = Icons.Outlined.Restaurant
                )
            }
        }
        item {
            FadeIn {
                MetricRow(
                    label = "Times Gone Over Budget",
                    value = sampleTimesOverBudget.toString(),
                    valueColor = StatRed,
                    icon = Icons.Outlined.MoneyOff,
                    iconColor = Color.Black
                )
            }
        }
        item {
            FadeIn(show = userScrolled) {
                MetricRow(
                    label = "Your Best Saving Period",
                    value = sampleBestPeriod,
                    valueColor = StatPurple
                )
            }
        }
        item {
            FadeIn(show = userScrolled) {
                MetricRow(
                    label = "Avg Overspent",
                    value = "\$" + twoDecimals(sampleAvgOverspent),
                    valueColor = StatRed
                )
            }
        }
        item {
            FadeIn(show = userScrolled) {
                MetricRow(
                    label = "Most Popular Merchant",
                    value = sampleTopMerchant,
                    valueColor = StatOrange,
                    valueIcon = Icons.Outlined.Storefront
                )
            }
        }

        // empty space at the bottom so the list can always be scrolled
        item {
            Spacer(modifier = Modifier.fillParentMaxHeight(0.5f))
        }
    }
}

// fades its content in once show is true (default: right away)
@Composable
fun FadeIn(show: Boolean = true, content: @Composable () -> Unit) {
    var visible by remember { mutableStateOf(false) }

    // starts the fade as soon as show is true
    LaunchedEffect(show) {
        if (show) {
            visible = true
        }
    }

    val fade by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "fade"
    )

    Box(modifier = Modifier.alpha(fade)) {
        content()
    }
}

// the dark "Overall Lifetime Savings" card
@Composable
fun SavingsCard(savingsText: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .background(NavyCard, RoundedCornerShape(24.dp))
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text("OVERALL LIFETIME SAVINGS", color = Color.White, fontSize = 12.sp)
            Text(
                text = "\$$savingsText",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "You've saved this much since starting with us!",
                color = Color.White,
                fontSize = 11.sp
            )
        }

        // white circle with the teal arrow
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.TrendingUp,
                contentDescription = null,
                tint = StatTeal,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}


@Composable
fun MetricRow(
    label: String,
    value: String,
    valueColor: Color,
    icon: ImageVector? = null,
    iconColor: Color = Color.Black,
    valueIcon: ImageVector? = null
) {
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
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // icon on the left (only some rows have one)
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Text(
                text = label,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                textAlign = if (icon != null) TextAlign.Center else TextAlign.Start,
                modifier = Modifier.weight(1f)
            )

            // small icon in front of the value (category / merchant rows)
            if (valueIcon != null) {
                Icon(
                    imageVector = valueIcon,
                    contentDescription = null,
                    tint = valueColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }

            Text(
                text = value,
                color = valueColor,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StatisticsPreview() {
    Statistics()
}