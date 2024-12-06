package com.example.crm.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.crm.components.BottomNavigationBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

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

            // Sección de citas próximas
            Text("Tus citas próximas:")
            Spacer(modifier = Modifier.height(8.dp))
            CitasList(navController)

            Spacer(modifier = Modifier.height(16.dp))

            // Sección de médicos favoritos
            Text("Tus médicos favoritos:")
            Spacer(modifier = Modifier.height(8.dp))
            FavoritosList(navController)
        }
    }
}

@Composable
fun CitasList(navController: NavController) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    val citas = remember { mutableStateListOf<Map<String, Any>>() }
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val today = dateFormat.format(Date())

    // Cargar citas desde Firestore
    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("citas")
            .whereEqualTo("paciente_id", userId)
            .get()
            .addOnSuccessListener { result ->
                citas.clear()
                result.forEach { doc ->
                    val data = doc.data.toMutableMap()
                    val fecha = data["fecha"] as? String
                    if (fecha != null && fecha >= today) { // Solo citas futuras
                        data["id"] = doc.id

                        // Obtener el nombre del médico
                        val medicoId = data["medico_id"] as? String
                        if (!medicoId.isNullOrEmpty()) {
                            db.collection("medicos").document(medicoId).get()
                                .addOnSuccessListener { medicoDoc ->
                                    data["medico_nombre"] = medicoDoc.getString("nombre") ?: "No asignado"
                                    citas.add(data)
                                }
                                .addOnFailureListener {
                                    data["medico_nombre"] = "No asignado"
                                    citas.add(data)
                                }
                        } else {
                            data["medico_nombre"] = "No asignado"
                            citas.add(data)
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                println("Error al cargar citas: ${e.message}")
            }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp) // Controla la altura para permitir espacio para otras secciones
    ) {
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

@Composable
fun FavoritosList(navController: NavController) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    val favoritos = remember { mutableStateListOf<Map<String, Any>>() }

    // Cargar favoritos desde Firestore
    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("favoritos")
            .whereEqualTo("paciente_id", userId)
            .get()
            .addOnSuccessListener { result ->
                favoritos.clear()
                result.forEach { doc ->
                    val medicoId = doc["medico_id"] as? String
                    if (!medicoId.isNullOrEmpty()) {
                        // Cargar los datos del médico favorito
                        db.collection("medicos").document(medicoId).get()
                            .addOnSuccessListener { medicoDoc ->
                                val medicoData = medicoDoc.data?.toMutableMap() ?: mutableMapOf()
                                medicoData["id"] = medicoDoc.id
                                favoritos.add(medicoData)
                            }
                            .addOnFailureListener {
                                println("Error al cargar médico favorito: ${it.message}")
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                println("Error al cargar favoritos: ${e.message}")
            }
    }

    LazyColumn {
        items(favoritos.toList()) { favorito ->
            FavoritoCard(favorito, navController)
        }
    }
}

@Composable
fun FavoritoCard(medico: Map<String, Any>, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                val medicoId = medico["id"] as? String
                if (medicoId != null) {
                    navController.navigate("medicoDetalle/$medicoId")
                } else {
                    println("Error: médico sin id.")
                }
            },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Nombre: ${medico["nombre"] ?: "Desconocido"}", style = MaterialTheme.typography.bodyLarge)
            Text("Apellidos: ${medico["apellidos"] ?: "Desconocido"}", style = MaterialTheme.typography.bodyLarge)
            Text("Correo: ${medico["correo"] ?: "No disponible"}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitaDetalleScreen(citaId: String, navController: NavController) {
    val cita = remember { mutableStateOf<Map<String, Any>?>(null) }
    val loading = remember { mutableStateOf(true) }
    val error = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(citaId) {
        val db = FirebaseFirestore.getInstance()
        val citaData = mutableMapOf<String, Any>()
        loading.value = true
        error.value = null

        try {
            // Cargar datos de la cita
            val citaSnapshot = db.collection("citas").document(citaId).get().await()
            val data = citaSnapshot.data
            if (data != null) {
                citaData["fecha"] = data["fecha"] ?: "No especificada"
                citaData["hora"] = data["hora"] ?: "No especificada"

                // Cargar información del médico
                val medicoId = data["medico_id"] as? String
                if (!medicoId.isNullOrEmpty()) {
                    val medicoSnapshot = db.collection("medicos").document(medicoId).get().await()
                    citaData["medico_nombre"] = medicoSnapshot.getString("nombre") ?: "No asignado"
                } else {
                    citaData["medico_nombre"] = "No asignado"
                }

                // Cargar información de la especialidad
                val especialidadId = data["especialidad_id"] as? String
                if (!especialidadId.isNullOrEmpty()) {
                    val especialidadSnapshot =
                        db.collection("especialidades").document(especialidadId).get().await()
                    citaData["especialidad"] = especialidadSnapshot.getString("nombre") ?: "No asignada"
                } else {
                    citaData["especialidad"] = "No asignada"
                }
            }
            cita.value = citaData
        } catch (e: Exception) {
            error.value = "Error al cargar los datos: ${e.message}"
        } finally {
            loading.value = false
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
        if (loading.value) {
            Text(
                text = "Cargando cita...",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        } else if (error.value != null) {
            Text(
                text = error.value ?: "Error desconocido",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
        } else {
            cita.value?.let {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                ) {
                    Text("Fecha: ${it["fecha"]}", style = MaterialTheme.typography.headlineSmall)
                    Text("Hora: ${it["hora"]}", style = MaterialTheme.typography.headlineSmall)
                    Text(
                        "Especialidad: ${it["especialidad"]}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        "Médico: ${it["medico_nombre"]}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}
