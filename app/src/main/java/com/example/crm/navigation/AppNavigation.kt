package com.example.crm.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.crm.activities.CitaDetalleScreen
import com.example.crm.auth.ForgotPasswordScreen
import com.example.crm.auth.LoginScreen
import com.example.crm.auth.RegisterScreen
import com.example.crm.activities.DashboardScreen
import com.example.crm.screens.SimuladorCitasScreen
import com.example.crm.screens.ProfileScreen
import com.example.screens.SplashScreen
import com.example.screens.WelcomeScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {
        // Pantalla de Splash
        composable("splash") {
            SplashScreen {
                navController.navigate("welcome") {
                    popUpTo("splash") { inclusive = true }
                }
            }
        }

        // Pantalla de bienvenida
        composable("welcome") {
            WelcomeScreen(
                onRegisterClick = { navController.navigate("signup") },
                onLoginClick = { navController.navigate("login") }
            )
        }

        // Pantalla de registro
        composable("signup") {
            RegisterScreen(
                onSignUpSuccess = { navController.navigate("login") },
                onBackToLoginClick = { navController.navigate("login") }
            )
        }

        // Pantalla de inicio de sesión
        composable("login") {
            LoginScreen(
                onLoginSuccess = { userData ->
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onRegisterClick = { navController.navigate("signup") },
                onForgotPasswordClick = { navController.navigate("forgotPassword") }
            )
        }



        // Pantalla de recuperación de contraseña
        composable("forgotPassword") {
            ForgotPasswordScreen(
                onSendLinkClick = {
                    navController.navigate("login") {
                        popUpTo("forgotPassword") { inclusive = true }
                    }
                },
                onBackClick = { navController.navigate("login") }
            )
        }

        // Dashboard principal
        composable("dashboard") {
            DashboardScreen(navController)
        }

        // Pantalla de perfil
        composable("profile") {
            ProfileScreen(navController)
        }

        // Pantalla de calendario
//        composable("calendar") {
//            CalendarScreen(navController)
//        }
        // Pantalla de simulador de citas
        composable("simuladorCitas") {
            SimuladorCitasScreen(navController)
        }
        composable("citaDetalle/{citaId}") { backStackEntry ->
            val citaId = backStackEntry.arguments?.getString("citaId") ?: return@composable
            CitaDetalleScreen(citaId = citaId, navController = navController)
        }


    }
}
