package com.example.crm.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.crm.auth.ForgotPasswordScreen
import com.example.crm.auth.LoginScreen
import com.example.crm.auth.SignUpScreen
import com.example.crm.ui.HomeScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { userData -> // userData es Map<String, String>
                    val userName = userData["name"] ?: "Usuario"
                    navController.navigate("home/$userName") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onSignUpClick = { navController.navigate("signup") },
                onForgotPasswordClick = { navController.navigate("forgotPassword") }
            )
        }
        composable("signup") {
            SignUpScreen(
                onSignUpSuccess = { navController.navigate("login") },
                onBackToLoginClick = { navController.navigate("login") }
            )
        }
        composable("home/{userName}") { backStackEntry ->
            val userName = backStackEntry.arguments?.getString("userName") ?: "Usuario"
            HomeScreen(userData = mapOf("name" to userName)) {
                FirebaseAuth.getInstance().signOut() // Directamente desde FirebaseAuth
                navController.navigate("login") { popUpTo("home") { inclusive = true } }
            }
        }
        composable("forgotPassword") {
            ForgotPasswordScreen(
                onBackToLoginClick = { navController.navigate("login") }
            )
        }

    }
}
