package com.example.crm.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.crm.screens.CitaDetalleScreen
import com.example.crm.auth.ForgotPasswordScreen
import com.example.crm.auth.LoginScreen
import com.example.crm.auth.RegisterScreen
import com.example.crm.activities.DashboardScreen
import com.example.crm.activities.MedicosEspecialidadesScreen
import com.example.crm.screens.MedicalCheckupsScreen
import com.example.crm.screens.MedicalSearchScreen
import com.example.crm.screens.MedicationsScreen
import com.example.crm.screens.SimuladorCitasScreen
import com.example.crm.screens.ProfileScreen
import com.example.crm.screens.RecentAppointmentsScreen
import com.example.screens.SplashScreen
import com.example.screens.WelcomeScreen

/**
 * **AppNavigation**
 *
 * Define toda la lógica de navegación de la aplicación usando el sistema de navegación de Jetpack Compose.
 * Cada pantalla tiene una **ruta** asociada, que se usa para navegar entre ellas.
 *
 * **Navegación:**
 * - **startDestination**: La pantalla inicial es `splash`.
 *
 * **Pantallas principales:**
 * - SplashScreen
 * - WelcomeScreen
 * - LoginScreen
 * - RegisterScreen
 * - ForgotPasswordScreen
 * - DashboardScreen
 * - ProfileScreen
 * - SimuladorCitasScreen
 * - CitaDetalleScreen
 * - MedicosEspecialidadesScreen
 * - RecentAppointmentsScreen
 * - MedicationsScreen
 * - MedicalCheckupsScreen
 * - AdminPanel
 * - MedicalSearchScreen
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController() // Controlador de la navegación

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
                onLoginSuccess = {
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
                onSendLinkClick = { navController.navigate("login") },
                onBackClick = { navController.navigate("login") }
            )
        }

        // Pantalla principal (Dashboard)
        composable("dashboard") {
            DashboardScreen(navController)
        }

        // Pantalla de perfil
        composable("profile") {
            ProfileScreen(navController)
        }

        // Pantalla de simulación de citas
        composable("simuladorCitas") {
            SimuladorCitasScreen(navController)
        }

        // Pantalla de citas recientes
        composable("appointments") {
            RecentAppointmentsScreen(navController)
        }

        // Pantalla de medicamentos recetados
        composable("medications") {
            MedicationsScreen(navController)
        }

        // Pantalla de chequeos médicos
        composable("checkups") {
            MedicalCheckupsScreen(navController)
        }

        // Pantalla de detalles de cita
        composable("citaDetalle/{citaId}") { backStackEntry ->
            val citaId = backStackEntry.arguments?.getString("citaId") ?: return@composable
            CitaDetalleScreen(citaId = citaId, navController = navController)
        }

        // Pantalla de búsqueda de médicos
        composable("medicalSearch") {
            MedicalSearchScreen(navController)
        }

        // Pantalla de gestión de médicos y especialidades
        composable("medicosEspecialidades") {
            MedicosEspecialidadesScreen()
        }
    }
}
