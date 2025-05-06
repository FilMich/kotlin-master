package com.example.semestralna_praca.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    data object Profile : BottomNavItem("profile", Icons.Default.Person, "Profil")
    data object Quests : BottomNavItem("quests", Icons.Default.List, "Questy")
    data object History : BottomNavItem("history", Icons.Default.CheckCircle, "História")
}
