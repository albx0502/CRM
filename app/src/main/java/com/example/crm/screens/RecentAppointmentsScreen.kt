package com.example.crm.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

/**
 * **RecentAppointmentsScreen**
 *
 * Pantalla que muestra las citas pasadas de un usuario.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentAppointmentsScreen(navController: NavController) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty() // ID de usuario autenticado
    val recentAppointments = remember { mutableStateListOf<Map<String, Any>>() } // Lista de citas pasadas
    val db = FirebaseFirestore.getInstance()

    // 🔄 Formato de fecha para comparar con la fecha actual
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val today = dateFormat.format(Date())

    // 🚀 Cargar citas pasadas del usuario desde Firestore
    LaunchedEffect(Unit) {
        db.collection("citas")
            .whereEqualTo("paciente_id", userId)
            .get()
            .addOnSuccessListener { result ->
                recentAppointments.clear()
                result.forEach { doc ->
                    val data = doc.data.toMutableMap()
                    val fecha = data["fecha"] as? String
                    if (fecha != null && fecha < today) { // Solo citas pasadas
                        data["id"] = doc.id

                        val medicoId = data["medico_id"] as? String
                        if (!medicoId.isNullOrEmpty()) {
                            db.collection("medicos").document(medicoId).get()
                                .addOnSuccessListener { medicoDoc ->
                                    data["medico_nombre"] = medicoDoc.getString("nombre") ?: "No asignado"
                                    recentAppointments.add(data)
                                }
                                .addOnFailureListener {
                                    data["medico_nombre"] = "No asignado"
                                    recentAppointments.add(data)
                                }
                        } else {
                            data["medico_nombre"] = "No asignado"
                            recentAppointments.add(data)
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                println("Error al cargar citas recientes: ${e.message}")
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Citas Recientes") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Mensaje cuando no hay citas recientes
            if (recentAppointments.isEmpty()) {
                Text(
                    text = "No hay citas recientes.",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                // Lista de citas recientes
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(recentAppointments) { appointment ->
                        RecentAppointmentCard(appointment, navController)
                    }
                }
            }
        }
    }
}

/**
 * **RecentAppointmentCard**
 *
 * Representa cada cita en la lista de citas recientes.
 */
@Composable
fun RecentAppointmentCard(appointment: Map<String, Any>, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val citaId = appointment["id"] as? String
                if (citaId != null) {
                    navController.navigate("citaDetalle/$citaId")
                }
            },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = "Fecha: ${appointment["fecha"] ?: "Desconocida"}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Hora: ${appointment["hora"] ?: "Desconocida"}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Médico: ${appointment["medico_nombre"] ?: "No asignado"}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
