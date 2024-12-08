package com.example.crm.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * **CitaDetalleScreen**
 *
 * Pantalla que muestra los detalles de una cita específica.
 *
 * **Parámetros:**
 * - `navController`: Controlador de navegación para manejar transiciones.
 * - `citaId`: ID de la cita cuyos detalles se mostrarán.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitaDetalleScreen(navController: NavController, citaId: String) {
    var citaDetalles by remember { mutableStateOf<Map<String, Any>?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Cargar detalles de la cita desde Firebase Firestore
    LaunchedEffect(citaId) {
        val db = FirebaseFirestore.getInstance()
        try {
            val doc = db.collection("citas").document(citaId).get().await()
            if (doc.exists()) {
                val data = doc.data?.toMutableMap() ?: mutableMapOf()
                // Obtener nombre del médico asociado
                val medicoId = data["medico_id"] as? String
                if (!medicoId.isNullOrEmpty()) {
                    val medicoDoc = db.collection("medicos").document(medicoId).get().await()
                    data["medico_nombre"] = medicoDoc.getString("nombre") ?: "No asignado"
                }
                citaDetalles = data
            } else {
                errorMessage = "No se encontraron detalles para esta cita."
            }
        } catch (e: Exception) {
            errorMessage = "Error al cargar detalles de la cita: ${e.message}"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles de la Cita") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                citaDetalles != null -> {
                    // Mostrar los detalles de la cita
                    Column(
                        verticalArrangement = Arrangement.Top,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Fecha: ${citaDetalles!!["fecha"] ?: "Sin fecha"}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Hora: ${citaDetalles!!["hora"] ?: "Sin hora"}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Médico: ${citaDetalles!!["medico_nombre"] ?: "No asignado"}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Descripción: ${citaDetalles!!["descripcion"] ?: "Sin descripción"}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                errorMessage != null -> {
                    // Mostrar mensaje de error
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                else -> {
                    // Mostrar indicador de carga
                    CircularProgressIndicator()
                }
            }
        }
    }
}
