package com.example.budgetpath

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.budgetpath.mobile.ui.theme.*

private const val PROFILE_INDEX = 4
private const val SETTINGS_INDEX = 5

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigation() {

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
                    Text(text = "")
                },
                navigationIcon = {
                    IconButton(onClick = { selectedIndex = SETTINGS_INDEX }) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Settings"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { selectedIndex = PROFILE_INDEX }) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = "Profile"
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
                containerColor = Color(0xFFF3EDF7)
            ) {
                navigationItems.forEachIndexed { screenIndex, item ->
                    NavigationBarItem(
                        selected = selectedIndex == screenIndex,
                        onClick = { selectedIndex = screenIndex },
                        icon = {
                            Text(
                                text = when (screenIndex) {
                                    0 -> "⌂"
                                    1 -> "$"
                                    2 -> "▤"
                                    else -> "↗"
                                }
                            )
                        },
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
            color = NavText
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                val screenTitle = when (selectedIndex) {
                    PROFILE_INDEX -> "Profile"
                    SETTINGS_INDEX -> "Settings"
                    else -> navigationItems[selectedIndex]
                }
                Text(screenTitle)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NavigationPreview() {
    Navigation()
}