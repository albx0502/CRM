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

/**
 * **DashboardActivity**
 *
 * Actividad principal que contiene la pantalla del Dashboard.
 * Muestra:
 * - Bienvenida personalizada.
 * - Lista de próximas citas del usuario.
 * - Lista de médicos favoritos.
 */
class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            DashboardScreen(navController)
        }
    }
}

/**
 * **DashboardScreen**
 *
 * Pantalla principal del Dashboard.
 *
 * **Funciones principales:**
 * - Muestra un mensaje de bienvenida.
 * - Lista de próximas citas (solo las futuras).
 * - Lista de médicos favoritos del usuario.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard", style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->
        // Contenedor principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            // Mensaje de bienvenida
            Text(
                text = "¡Bienvenido!",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Sección de citas próximas
            SectionHeader(title = "Tus citas próximas")
            Spacer(modifier = Modifier.height(8.dp))
            CitasList(navController) // Llamada al componente de citas

            Spacer(modifier = Modifier.height(16.dp))

            // Sección de médicos favoritos
            SectionHeader(title = "Tus médicos favoritos")
            Spacer(modifier = Modifier.height(8.dp))
            FavoritosList(navController) // Llamada al componente de favoritos
        }
    }
}



/**
 * **SectionHeader**
 *
 * Componente reutilizable que muestra un separador con un título estilizado.
 *
 * **Parámetros:**
 * - `title`: Título de la sección.
 */
@Composable
fun SectionHeader(title: String) {
    Divider(color = MaterialTheme.colorScheme.primary, thickness = 1.dp)
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground
    )
}

/**
 * **CitasList**
 *
 * Componente que muestra una lista de las próximas citas del usuario.
 *
 * **Funciones principales:**
 * - Filtra y muestra solo las citas futuras.
 * - Permite al usuario navegar al detalle de cada cita.
 */
@Composable
fun CitasList(navController: NavController) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    val citas = remember { mutableStateListOf<Map<String, Any>>() }
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val today = dateFormat.format(Date())

    // Cargar citas desde Firebase Firestore
    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        try {
            val result = db.collection("citas")
                .whereEqualTo("paciente_id", userId)
                .get()
                .await()

            citas.clear()
            result.forEach { doc ->
                val data = doc.data.toMutableMap()
                val fecha = data["fecha"] as? String
                if (fecha != null && fecha >= today) { // Filtrar solo citas futuras
                    data["id"] = doc.id

                    // Obtener nombre del médico
                    val medicoId = data["medico_id"] as? String
                    if (!medicoId.isNullOrEmpty()) {
                        val medicoDoc = db.collection("medicos").document(medicoId).get().await()
                        data["medico_nombre"] = medicoDoc.getString("nombre") ?: "No asignado"
                    } else {
                        data["medico_nombre"] = "No asignado"
                    }
                    citas.add(data)
                }
            }
        } catch (e: Exception) {
            println("Error al cargar citas: ${e.message}")
        }
    }

    // Lista de citas con altura limitada
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp) // Altura controlada para evitar solapamiento
    ) {
        items(citas.toList()) { cita ->
            CitaCard(cita, navController)
        }
    }
}

/**
 * **CitaCard**
 *
 * Representa cada cita en la lista de próximas citas.
 *
 * **Parámetros:**
 * - `cita`: Datos de la cita.
 * - `navController`: Controlador de navegación para redirigir al detalle de la cita.
 */
@Composable
fun CitaCard(cita: Map<String, Any>, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                val citaId = cita["id"] as? String
                if (citaId != null) {
                    navController.navigate("citaDetalle/$citaId") // Navegar a detalles de la cita
                }
            },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Fecha: ${cita["fecha"] ?: "Sin fecha"}", style = MaterialTheme.typography.bodyLarge)
            Text("Hora: ${cita["hora"] ?: "Sin hora"}", style = MaterialTheme.typography.bodyLarge)
            Text("Médico: ${cita["medico_nombre"] ?: "No asignado"}", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

/**
 * **FavoritosList**
 *
 * Componente que muestra una lista de médicos favoritos.
 *
 * **Funciones principales:**
 * - Carga los médicos favoritos desde Firebase Firestore.
 * - Permite navegar al detalle del médico.
 */
@Composable
fun FavoritosList(navController: NavController) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    val favoritos = remember { mutableStateListOf<Map<String, Any>>() }

    // Cargar médicos favoritos desde Firebase Firestore
    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        try {
            val result = db.collection("favoritos")
                .whereEqualTo("paciente_id", userId)
                .get()
                .await()

            favoritos.clear()
            result.forEach { doc ->
                val medicoId = doc["medico_id"] as? String
                if (!medicoId.isNullOrEmpty()) {
                    val medicoDoc = db.collection("medicos").document(medicoId).get().await()
                    val medicoData = medicoDoc.data?.toMutableMap() ?: mutableMapOf()
                    medicoData["id"] = medicoDoc.id
                    favoritos.add(medicoData)
                }
            }
        } catch (e: Exception) {
            println("Error al cargar favoritos: ${e.message}")
        }
    }

    // Lista de médicos favoritos
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp) // Altura controlada para evitar solapamiento
    ) {
        items(favoritos.toList()) { favorito ->
            FavoritoCard(favorito, navController)
        }
    }
}

/**
 * **FavoritoCard**
 *
 * Representa cada médico favorito en la lista.
 *
 * **Parámetros:**
 * - `medico`: Información del médico.
 * - `navController`: Controlador de navegación para redirigir al detalle del médico.
 */
@Composable
fun FavoritoCard(medico: Map<String, Any>, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                val medicoId = medico["id"] as? String
                if (medicoId != null) {
                    navController.navigate("medicoDetalle/$medicoId") // Navegar a detalles del médico
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
