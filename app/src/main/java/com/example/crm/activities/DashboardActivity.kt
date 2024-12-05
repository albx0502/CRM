package com.example.crm.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.crm.components.BottomNavigationBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.foundation.lazy.items

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            DashboardScreen(navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Dashboard") }) },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Text(text = "¡Bienvenido!", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Tus citas próximas:")
            Spacer(modifier = Modifier.height(8.dp))
            CitasList(navController)
        }
    }
}

@Composable
fun CitasList(navController: NavController) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    val citas = remember { mutableStateListOf<Map<String, Any>>() }

    // Cargar citas desde Firestore
    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("citas")
            .whereEqualTo("paciente_id", userId)
            .get()
            .addOnSuccessListener { result ->
                citas.clear()
                result.forEach { doc ->
                    val citaData = doc.data.toMutableMap()
                    citaData["id"] = doc.id

                    // Obtener nombre del médico si está disponible
                    val medicoId = citaData["medico_id"] as? String
                    if (!medicoId.isNullOrEmpty()) {
                        db.collection("medicos").document(medicoId).get()
                            .addOnSuccessListener { medicoDoc ->
                                citaData["medico_nombre"] =
                                    medicoDoc.getString("nombre") ?: "No asignado"
                                citas.add(citaData)
                            }
                            .addOnFailureListener {
                                citaData["medico_nombre"] = "No asignado"
                                citas.add(citaData)
                            }
                    } else {
                        citaData["medico_nombre"] = "No asignado"
                        citas.add(citaData)
                    }
                }
            }
            .addOnFailureListener { e ->
                println("Error al cargar citas: ${e.message}")
            }
    }

    // Mostrar citas
    LazyColumn {
        items(citas.toList()) { cita -> // Usa toList() para asegurarte de que SnapshotStateList sea iterable.
            CitaCard(cita, navController)
        }
    }

}

@Composable
fun CitaCard(cita: Map<String, Any>, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                val citaId = cita["id"] as? String
                if (citaId != null) {
                    navController.navigate("citaDetalle/$citaId")
                } else {
                    println("Error: cita sin id.")
                }
            },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Fecha: ${cita["fecha"] ?: "Sin fecha"}", style = MaterialTheme.typography.bodyLarge)
            Text("Hora: ${cita["hora"] ?: "Sin hora"}", style = MaterialTheme.typography.bodyLarge)
            Text(
                "Médico: ${cita["medico_nombre"] ?: "No asignado"}",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitaDetalleScreen(citaId: String, navController: NavController) {
    val cita = remember { mutableStateOf<Map<String, Any>?>(null) }

    // Cargar detalles de la cita
    LaunchedEffect(citaId) {
        val db = FirebaseFirestore.getInstance()
        db.collection("citas").document(citaId)
            .get()
            .addOnSuccessListener { document ->
                cita.value = document.data
            }
            .addOnFailureListener { e ->
                println("Error al cargar cita: ${e.message}")
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Cita") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        cita.value?.let { cita ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                Text("Fecha: ${cita["fecha"] ?: "Sin fecha"}", style = MaterialTheme.typography.headlineSmall)
                Text("Hora: ${cita["hora"] ?: "Sin hora"}", style = MaterialTheme.typography.headlineSmall)
                Text("Especialidad: ${cita["especialidad"] ?: "No asignada"}", style = MaterialTheme.typography.bodyLarge)
                Text("Médico: ${cita["medico_nombre"] ?: "No asignado"}", style = MaterialTheme.typography.bodyLarge)
            }
        } ?: run {
            Text("Cargando cita...", modifier = Modifier.padding(16.dp))
        }
    }
}
