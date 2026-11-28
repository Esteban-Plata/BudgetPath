package com.budgetpath.mobile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.budgetpath.mobile.ui.theme.*


private const val PROFILE_INDEX = 4
private const val SETTINGS_INDEX = 5

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigation(onLogout: () -> Unit = {}) {

    var selectedIndex by remember { mutableIntStateOf(0) }

    val navigationItems = listOf(
        "Home",
        "Transactions",
        "Accounts",
        "Statistics"
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "BudgetPath",
                        fontFamily = FontFamily.Cursive,
                        fontWeight = FontWeight.Bold,
                        fontSize = 35.sp)
                },
                navigationIcon = {
                    IconButton(onClick = { selectedIndex = SETTINGS_INDEX }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                },

                actions = {
                    IconButton(onClick = { selectedIndex = PROFILE_INDEX }) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = "Profile",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ScreenBackground,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black,
                    actionIconContentColor = Color.Black
                )
            )
        },

        bottomBar = {
            NavigationBar(
                containerColor = BottomBarBackground
            ) {
                navigationItems.forEachIndexed { screenIndex, item ->
                    NavigationBarItem(
                        selected = selectedIndex == screenIndex,
                        onClick = { selectedIndex = screenIndex },
                        icon = {
                            Icon(
                                imageVector = when (screenIndex) {
                                    0 -> Icons.Outlined.Home
                                    1 -> Icons.Outlined.AttachMoney
                                    2 -> Icons.Outlined.Storage
                                    else -> Icons.Outlined.TrendingUp
                                },
                                contentDescription = item
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = BottomBarIndicator
                        ),
                        label = { Text(item) }
                    )
                }
            }
        }

    ) { innerPadding ->

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = ScreenBackground
        ) {
            // the Home tab shows the dashboard
            if (selectedIndex == 0) {
                Dashboard()
            }
            // the Profile tab has the logout button
            else if (selectedIndex == PROFILE_INDEX) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Profile")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onLogout) {
                        Text("Logout")
                    }
                }
            }
            // every other tab is still a placeholder
            else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    val screenTitle = when (selectedIndex) {
                        SETTINGS_INDEX -> "Settings"
                        else -> navigationItems[selectedIndex]
                    }
                    Text(screenTitle)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NavigationPreview() {
    Navigation()
}