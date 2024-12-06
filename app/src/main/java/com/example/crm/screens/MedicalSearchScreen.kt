package com.example.crm.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.crm.components.SimpleDropdownSelector
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalSearchScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    val medicos = remember { mutableStateListOf<Map<String, Any>>() }
    val especialidades = remember { mutableStateListOf<Map<String, Any>>() }
    val favoritos = remember { mutableStateListOf<String>() } // Lista de IDs de médicos favoritos


    // Filtros
    var especialidadSeleccionada by remember { mutableStateOf("") }
    var medicoBuscado by remember { mutableStateOf("") }
    var mostrarFavoritos by remember { mutableStateOf(false) } // Nuevo estado para el filtro de favoritos


    // Cargar médicos, especialidades y favoritos
    LaunchedEffect(Unit) {
        db.collection("medicos")
            .get()
            .addOnSuccessListener { result ->
                medicos.clear()
                medicos.addAll(result.map { it.data + ("id" to it.id) })
            }
            .addOnFailureListener { e ->
                println("Error al cargar médicos: ${e.message}")
            }

        db.collection("especialidades")
            .get()
            .addOnSuccessListener { result ->
                especialidades.clear()
                especialidades.addAll(result.map { it.data + ("id" to it.id) })
            }
            .addOnFailureListener { e ->
                println("Error al cargar especialidades: ${e.message}")
            }

        // Cargar favoritos del usuario actual
        db.collection("favoritos")
            .whereEqualTo("paciente_id", userId)
            .get()
            .addOnSuccessListener { result ->
                favoritos.clear()
                favoritos.addAll(result.map { it["medico_id"].toString() })
            }
            .addOnFailureListener { e ->
                println("Error al cargar favoritos: ${e.message}")
            }
    }

    // Función para manejar la lógica de favoritos
    fun toggleFavorito(medicoId: String, isFavorito: Boolean) {
        if (isFavorito) {
            // Eliminar de favoritos
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
                .addOnFailureListener { e ->
                    println("Error al eliminar de favoritos: ${e.message}")
                }
        } else {
            // Añadir a favoritos
            val favorito = mapOf("paciente_id" to userId, "medico_id" to medicoId)
            db.collection("favoritos").add(favorito)
                .addOnSuccessListener {
                    favoritos.add(medicoId)
                }
                .addOnFailureListener { e ->
                    println("Error al añadir a favoritos: ${e.message}")
                }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Buscar Médicos") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Filtro por especialidad
            SimpleDropdownSelector(
                label = "Selecciona una especialidad",
                options = especialidades.map { it["nombre"].toString() },
                selectedOption = especialidades.firstOrNull { it["id"] == especialidadSeleccionada }?.get("nombre").toString(),
                onOptionSelected = { selectedName ->
                    especialidadSeleccionada = especialidades.firstOrNull { it["nombre"] == selectedName }?.get("id").toString()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Buscar médico por nombre
            OutlinedTextField(
                value = medicoBuscado,
                onValueChange = { medicoBuscado = it },
                label = { Text("Buscar por nombre") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para alternar favoritos
            Button(
                onClick = { mostrarFavoritos = !mostrarFavoritos },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (mostrarFavoritos) "Mostrar Todos" else "Mostrar Favoritos")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Lista de médicos
            LazyColumn {
                items(
                    medicos.filter {
                        (!mostrarFavoritos || favoritos.contains(it["id"].toString())) &&
                                (especialidadSeleccionada.isEmpty() || it["especialidad_id"] == especialidadSeleccionada) &&
                                (medicoBuscado.isEmpty() || (it["nombre"] as? String)?.contains(medicoBuscado, ignoreCase = true) == true)
                    }
                ) { medico ->
                    val isFavorito = favoritos.contains(medico["id"].toString())
                    MedicoCard(medico = medico, isFavorito = isFavorito, onFavoritoClick = ::toggleFavorito)
                }
            }
        }
    }
}


@Composable
fun MedicoCard(medico: Map<String, Any>, isFavorito: Boolean, onFavoritoClick: (String, Boolean) -> Unit) {
    val medicoId = medico["id"].toString() // ID del médico
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

