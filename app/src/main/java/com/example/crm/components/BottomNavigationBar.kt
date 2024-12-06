package com.example.crm.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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

@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        tonalElevation = 8.dp
    ) {
        // Botón de inicio
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_home),
                    contentDescription = "Home"
                )
            },
            label = { Text("Home") },
            selected = currentRoute == "dashboard",
            onClick = {
                if (currentRoute != "dashboard") {
                    navController.navigate("dashboard") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                }
            }
        )

        // Botón de citas
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_calendar),
                    contentDescription = "Simulador Citas"
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

        // Botón de perfil
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_profile),
                    contentDescription = "Perfil"
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

        // Botón de médicos y especialidades
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_doctor), // Asume que tienes un icono llamado ic_doctor
                    contentDescription = "Médicos"
                )
            },
            label = { Text("Médicos") },
            selected = currentRoute == "medicalSearch",
            onClick = {
                if (currentRoute != "medicalSearch") {
                    navController.navigate("medicalSearch") {
                        popUpTo("dashboard")
                    }
                }
            }
        )
    }
}
