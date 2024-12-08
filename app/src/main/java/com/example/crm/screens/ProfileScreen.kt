package com.example.crm.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.crm.R
import com.example.crm.components.BottomNavigationBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * **ProfileScreen**
 *
 * Pantalla de perfil del usuario donde se muestra:
 * - Imagen de perfil.
 * - Nombre y apellidos del usuario.
 * - Opciones de acceso rápido (Citas recientes, Medicamentos, Chequeos médicos, etc.).
 * - Acceso exclusivo al **Panel Administrativo** si el usuario tiene el rol de **admin**.
 * - Botones de **Cerrar sesión** y **Eliminar cuenta** con sus respectivas funciones.
 */
@Composable
fun ProfileScreen(navController: NavController) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    val userData = remember { mutableStateOf<Map<String, Any>?>(null) }
    val db = FirebaseFirestore.getInstance()
    var userRole by remember { mutableStateOf<String?>(null) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }

    // 🔄 Cargar datos del usuario desde Firestore
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            db.collection("usuarios").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    userData.value = document.data
                    userRole = document.getString("rol") // Se asume que el campo "rol" existe en la colección "usuarios"
                }
                .addOnFailureListener { e ->
                    println("Error al cargar datos del usuario: ${e.message}")
                }
        }
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
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
            // 📷 Imagen de perfil con diseño circular y sombra
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .shadow(8.dp, RoundedCornerShape(60.dp))
                    .clip(RoundedCornerShape(60.dp))
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_profile_avatar), // Icono por defecto
                    contentDescription = "Avatar del usuario",
                    modifier = Modifier.size(100.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 📝 Información de nombre y apellidos del usuario
            val userName = userData.value?.get("nombre")?.toString() ?: "Cargando..."
            val userLastName = userData.value?.get("apellidos")?.toString() ?: ""
            Text(
                text = "$userName $userLastName",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 🔗 Opciones de navegación del perfil
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

            // 🚪 Mostrar opción de **Panel Administrativo** solo si el usuario tiene el rol "admin"
            if (userRole == "admin") {
                ProfileOptionItem(
                    title = "Panel Administrativo",
                    icon = painterResource(id = R.drawable.ic_team_member),
                    onClick = { navController.navigate("adminPanel") }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 🔐 Botón para cerrar sesión
            Button(
                onClick = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("login") {
                        popUpTo("profile") { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    "Cerrar sesión",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ❌ Botón para eliminar la cuenta
            Button(
                onClick = { showDeleteAccountDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text(
                    "Eliminar cuenta",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }

    // 📢 Diálogo de confirmación para eliminar la cuenta
    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro de que deseas eliminar tu cuenta? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        deleteAccount(navController)
                        showDeleteAccountDialog = false
                    }
                ) {
                    Text("Sí, eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteAccountDialog = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

/**
 * **deleteAccount**
 *
 * Elimina la cuenta del usuario de Firebase Authentication y la colección "usuarios" en Firestore.
 */
fun deleteAccount(navController: NavController) {
    val user = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()
    val userId = user?.uid ?: return

    user.delete().addOnCompleteListener { task ->
        if (task.isSuccessful) {
            // Eliminar la información de la colección "usuarios" en Firestore
            db.collection("usuarios").document(userId)
                .delete()
                .addOnSuccessListener {
                    navController.navigate("login") {
                        popUpTo("profile") { inclusive = true }
                    }
                }
                .addOnFailureListener { e ->
                    println("Error al eliminar los datos del usuario: ${e.message}")
                }
        } else {
            println("Error al eliminar la cuenta de autenticación: ${task.exception?.message}")
        }
    }
}

/**
 * **ProfileOptionItem**
 *
 * Representa cada opción del perfil que se muestra al usuario (por ejemplo, "Citas Recientes", "Chequeos Médicos", etc.).
 */
@Composable
fun ProfileOptionItem(title: String, icon: Painter, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
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
