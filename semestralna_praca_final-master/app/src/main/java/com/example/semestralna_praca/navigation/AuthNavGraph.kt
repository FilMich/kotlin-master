package com.example.semestralna_praca.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.semestralna_praca.ui.theme.screens.AssessmentScreen
import com.example.semestralna_praca.ui.theme.screens.LoginScreen
import com.example.semestralna_praca.ui.theme.screens.RegisterScreen
import com.example.semestralna_praca.ui.theme.screens.WelcomeScreen
import com.example.semestralna_praca.viewmodel.HomeViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun AuthNavGraph(navController: NavHostController) {
    val isLoggedIn = Firebase.auth.currentUser != null
    val startRoute = if (isLoggedIn) "main" else "welcome"

    NavHost(navController = navController, startDestination = startRoute) {
        composable("welcome") { WelcomeScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("assessment") { AssessmentScreen(navController) }
        composable("main") { val homeViewModel: HomeViewModel = viewModel()
            MainScreen(rootNavController = navController, viewModel = homeViewModel) }
    }
}
