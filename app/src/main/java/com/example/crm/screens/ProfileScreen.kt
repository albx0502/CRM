package com.example.crm.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.crm.R
import com.example.crm.components.BottomNavigationBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ProfileScreen(navController: NavController) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    val userData = remember { mutableStateOf<Map<String, Any>?>(null) }
    val db = FirebaseFirestore.getInstance()
    var userRole by remember { mutableStateOf<String?>(null) }

    // Cargar datos del usuario desde Firestore
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            db.collection("usuarios").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    userData.value = document.data
                    userRole = document.getString("rol") // Asume que el campo "rol" existe
                }
                .addOnFailureListener { e ->
                    println("Error al cargar datos del usuario: ${e.message}")
                }
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Imagen de perfil
            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_profile_avatar),
                    contentDescription = "Avatar del usuario",
                    modifier = Modifier.size(100.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nombre del usuario
            val userName = userData.value?.get("nombre")?.toString() ?: "Cargando..."
            val userLastName = userData.value?.get("apellidos")?.toString() ?: ""
            Text(
                text = "$userName $userLastName",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Opciones del perfil
            ProfileOptionItem(
                title = "Citas recientes",
                icon = painterResource(id = R.drawable.ic_recent_appointments),
                onClick = { navController.navigate("appointments") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ProfileOptionItem(
                title = "Medicamentos Recetados",
                icon = painterResource(id = R.drawable.ic_medicine),
                onClick = { navController.navigate("medications") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ProfileOptionItem(
                title = "Chequeos médicos",
                icon = painterResource(id = R.drawable.ic_medical_checkups),
                onClick = { navController.navigate("checkups") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Mostrar "Panel Administrativo" solo para administradores
            if (userRole == "admin") {
                ProfileOptionItem(
                    title = "Panel Administrativo",
                    icon = painterResource(id = R.drawable.ic_team_member),
                    onClick = { navController.navigate("adminPanel") }
                )
            }
        }
    }
}

@Composable
fun ProfileOptionItem(title: String, icon: Painter, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Image(
                painter = icon,
                contentDescription = title,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
