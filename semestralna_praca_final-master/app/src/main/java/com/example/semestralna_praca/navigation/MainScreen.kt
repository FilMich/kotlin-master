package com.example.semestralna_praca.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.semestralna_praca.ui.theme.screens.HistoryScreen
import com.example.semestralna_praca.ui.theme.screens.ProfileScreen
import com.example.semestralna_praca.ui.theme.screens.QuestsScreen
import com.example.semestralna_praca.viewmodel.HomeViewModel

@Composable
fun MainScreen(
    rootNavController: NavHostController, // ✅ dôležité zachovať
    viewModel: HomeViewModel
) {
    val navController = rememberNavController()
    val items = listOf(
        BottomNavItem.Profile,
        BottomNavItem.Quests,
        BottomNavItem.History
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentRoute == item.route,
                        onClick = {
                            // 🔁 Vždy refreshni podľa záložky
                            when (item) {
                                BottomNavItem.Quests -> {
                                    viewModel.loadUserStats()
                                    viewModel.loadActiveQuests()
                                }
                                BottomNavItem.Profile -> {
                                    viewModel.loadUserStats()
                                }
                                BottomNavItem.History -> {
                                    viewModel.loadCompletedQuests()
                                }
                            }

                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Profile.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Profile.route) {
                ProfileScreen(navController = rootNavController, viewModel = viewModel) // rootNavController môžeš pridať ak budeš potrebovať
            }
            composable(BottomNavItem.Quests.route) {
                QuestsScreen( viewModel = viewModel)
            }
            composable(BottomNavItem.History.route) {
                HistoryScreen( viewModel = viewModel)
            }
        }
    }
}
