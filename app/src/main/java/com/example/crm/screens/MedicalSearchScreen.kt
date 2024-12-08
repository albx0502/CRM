package com.example.crm.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.crm.components.BottomNavigationBar
import com.example.crm.components.SimpleDropdownSelector
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * **MedicalSearchScreen**
 *
 * Pantalla que permite al usuario buscar médicos, filtrarlos por nombre o especialidad,
 * y marcar o desmarcar como favoritos.
 *
 * **Características:**
 * - Filtro por especialidad.
 * - Búsqueda por nombre.
 * - Alternar entre mostrar todos los médicos o solo los favoritos.
 * - Agregar o eliminar médicos de favoritos.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalSearchScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty() // ID del usuario autenticado
    val medicos = remember { mutableStateListOf<Map<String, Any>>() } // Lista de médicos
    val especialidades = remember { mutableStateListOf<Map<String, Any>>() } // Lista de especialidades
    val favoritos = remember { mutableStateListOf<String>() } // IDs de favoritos

    // Estados para filtros y búsqueda
    var especialidadSeleccionada by remember { mutableStateOf("") }
    var medicoBuscado by remember { mutableStateOf("") }
    var mostrarFavoritos by remember { mutableStateOf(false) }

    // 🔄 Cargar médicos, especialidades y favoritos al iniciar
    LaunchedEffect(Unit) {
        // Cargar médicos
        db.collection("medicos").get()
            .addOnSuccessListener { result ->
                medicos.clear()
                medicos.addAll(result.map { it.data + ("id" to it.id) })
            }
            .addOnFailureListener { println("Error al cargar médicos: ${it.message}") }

        // Cargar especialidades
        db.collection("especialidades").get()
            .addOnSuccessListener { result ->
                especialidades.clear()
                especialidades.addAll(result.map { it.data + ("id" to it.id) })
            }
            .addOnFailureListener { println("Error al cargar especialidades: ${it.message}") }

        // Cargar favoritos
        db.collection("favoritos").whereEqualTo("paciente_id", userId).get()
            .addOnSuccessListener { result ->
                favoritos.clear()
                favoritos.addAll(result.map { it["medico_id"].toString() })
            }
            .addOnFailureListener { println("Error al cargar favoritos: ${it.message}") }
    }

    // ⚙️ Alternar estado de favorito
    fun toggleFavorito(medicoId: String, isFavorito: Boolean) {
        if (isFavorito) {
            db.collection("favoritos")
                .whereEqualTo("paciente_id", userId)
                .whereEqualTo("medico_id", medicoId)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        db.collection("favoritos").document(document.id).delete()
                    }
                    favoritos.remove(medicoId)
                }
                .addOnFailureListener { println("Error al eliminar de favoritos: ${it.message}") }
        } else {
            val favorito = mapOf("paciente_id" to userId, "medico_id" to medicoId)
            db.collection("favoritos").add(favorito)
                .addOnSuccessListener { favoritos.add(medicoId) }
                .addOnFailureListener { println("Error al añadir a favoritos: ${it.message}") }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buscar Médicos") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) } // Barra inferior de navegación
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Selector de especialidad
            SimpleDropdownSelector(
                label = "Selecciona una especialidad",
                options = especialidades.map { it["nombre"].toString() },
                selectedOption = especialidades.firstOrNull { it["id"] == especialidadSeleccionada }?.get("nombre").toString(),
                onOptionSelected = { selectedName ->
                    especialidadSeleccionada = especialidades.firstOrNull { it["nombre"] == selectedName }?.get("id").toString()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de búsqueda por nombre
            OutlinedTextField(
                value = medicoBuscado,
                onValueChange = { medicoBuscado = it },
                label = { Text("Buscar por nombre") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para alternar entre todos los médicos y favoritos
            Button(
                onClick = { mostrarFavoritos = !mostrarFavoritos },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (mostrarFavoritos) "Mostrar Todos" else "Mostrar Favoritos")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Lista de médicos filtrada
            LazyColumn {
                items(
                    medicos.filter {
                        (!mostrarFavoritos || favoritos.contains(it["id"].toString())) &&
                                (especialidadSeleccionada.isEmpty() || it["especialidad_id"] == especialidadSeleccionada) &&
                                (medicoBuscado.isEmpty() || (it["nombre"] as? String)?.contains(medicoBuscado, ignoreCase = true) == true)
                    }
                ) { medico ->
                    val isFavorito = favoritos.contains(medico["id"].toString())
                    MedicoCard(
                        medico = medico,
                        isFavorito = isFavorito,
                        onFavoritoClick = ::toggleFavorito
                    )
                }
            }
        }
    }
}

/**
 * **MedicoCard**
 *
 * Tarjeta para mostrar la información de un médico con opción de marcar como favorito.
 */
@Composable
fun MedicoCard(medico: Map<String, Any>, isFavorito: Boolean, onFavoritoClick: (String, Boolean) -> Unit) {
    val medicoId = medico["id"].toString()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Nombre: ${medico["nombre"] ?: "Desconocido"}", style = MaterialTheme.typography.bodyLarge)
            Text("Apellidos: ${medico["apellidos"] ?: "Desconocido"}", style = MaterialTheme.typography.bodyLarge)
            Text("Correo: ${medico["correo"] ?: "No disponible"}", style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { onFavoritoClick(medicoId, isFavorito) }
            ) {
                Text(if (isFavorito) "Eliminar de Favoritos" else "Añadir a Favoritos")
            }
        }
    }
}
