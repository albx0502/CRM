package com.example.crm.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.crm.R

/**
 * **BottomNavigationBar**
 *
 * Barra de navegación inferior que permite a los usuarios moverse entre las pantallas principales
 * de la aplicación.
 *
 * @param navController Controlador de navegación para cambiar de pantalla.
 */
@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color.White, // Fondo blanco para la barra
        modifier = Modifier
            .fillMaxWidth() // Ancho completo de la pantalla
            .height(64.dp), // Altura de la barra
        tonalElevation = 8.dp // Elevación para una sombra sutil
    ) {
        /**
         * Botón de Inicio (Dashboard)
         */
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_home),
                    contentDescription = "Inicio",
                    tint = Color.Unspecified, // No aplica ningún color
                    modifier = Modifier.size(24.dp) // Tamaño reducido

                )
            },
            label = { Text("Inicio") },
            selected = currentRoute == "dashboard",
            onClick = {
                if (currentRoute != "dashboard") {
                    navController.navigate("dashboard") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                }
            }
        )

        /**
         * Botón de Simulador de Citas
         */
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_calendar),
                    contentDescription = "Citas",
                    tint = Color.Unspecified, // No aplica ningún color
                    modifier = Modifier.size(24.dp) // Tamaño reducido

                )
            },
            label = { Text("Citas") },
            selected = currentRoute == "simuladorCitas",
            onClick = {
                if (currentRoute != "simuladorCitas") {
                    navController.navigate("simuladorCitas") {
                        popUpTo("dashboard")
                    }
                }
            }
        )

        /**
         * Botón de Buscar (Búsqueda de Médicos)
         */
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = "Buscar",
                    tint = Color.Unspecified, // No aplica ningún color
                    modifier = Modifier.size(24.dp) // Tamaño reducido

                )
            },
            label = { Text("Buscar") },
            selected = currentRoute == "medicalSearch",
            onClick = {
                if (currentRoute != "medicalSearch") {
                    navController.navigate("medicalSearch") {
                        popUpTo("dashboard")
                    }
                }
            }
        )

        /**
         * Botón de Perfil
         */
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_profile),
                    contentDescription = "Perfil",
                    tint = Color.Unspecified, // No aplica ningún color
                    modifier = Modifier.size(24.dp) // Tamaño reducido

                )
            },
            label = { Text("Perfil") },
            selected = currentRoute == "profile",
            onClick = {
                if (currentRoute != "profile") {
                    navController.navigate("profile") {
                        popUpTo("dashboard")
                    }
                }
            }
        )
    }
}
