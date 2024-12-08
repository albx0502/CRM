package com.example.crm.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.crm.components.BottomNavigationBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * **MedicationsScreen**
 *
 * Pantalla para gestionar medicamentos del usuario y los disponibles.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationsScreen(navController: NavController) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    val medicamentosDisponibles = remember { mutableStateListOf<Map<String, String?>>() }
    val medicamentosUsuario = remember { mutableStateListOf<Map<String, String?>>() }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val medicamentosNombresUsuario = remember { mutableStateListOf<String>() }

    // Cargar datos desde Firestore
    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()

        // Cargar medicamentos disponibles
        db.collection("medicamentos_disponibles").get()
            .addOnSuccessListener { result ->
                medicamentosDisponibles.clear()
                medicamentosDisponibles.addAll(
                    result.map { document ->
                        mapOf(
                            "id" to document.id,
                            "nombre" to document.getString("nombre"),
                            "descripcion" to document.getString("descripcion"),
                            "indicaciones" to document.getString("indicaciones")
                        )
                    }
                )
            }
            .addOnFailureListener { e ->
                errorMessage.value = "Error al cargar medicamentos disponibles: ${e.message}"
            }

        // Cargar medicamentos del usuario
        db.collection("medicamentos").whereEqualTo("paciente_id", userId).get()
            .addOnSuccessListener { result ->
                medicamentosUsuario.clear()
                medicamentosNombresUsuario.clear()
                medicamentosUsuario.addAll(
                    result.map { document ->
                        val nombre = document.getString("nombre") ?: "Desconocido"
                        medicamentosNombresUsuario.add(nombre)
                        mapOf(
                            "id" to document.id,
                            "nombre" to nombre,
                            "descripcion" to document.getString("descripcion"),
                            "indicaciones" to document.getString("indicaciones")
                        )
                    }
                )
            }
            .addOnFailureListener { e ->
                errorMessage.value = "Error al cargar tus medicamentos: ${e.message}"
            }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Medicamentos Recetados") }) },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Mostrar mensaje de error
            errorMessage.value?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Lista de medicamentos del usuario
            Text("Tus Medicamentos", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(medicamentosUsuario) { medicamento ->
                    MedicamentoUsuarioCard(
                        medicamento = medicamento,
                        onDeleteClick = { medicamentoId ->
                            val db = FirebaseFirestore.getInstance()
                            db.collection("medicamentos").document(medicamentoId).delete()
                                .addOnSuccessListener {
                                    medicamentosUsuario.removeIf { it["id"] == medicamentoId }
                                    medicamentosNombresUsuario.remove(medicamento["nombre"])
                                }
                                .addOnFailureListener { e ->
                                    errorMessage.value = "Error al eliminar medicamento: ${e.message}"
                                }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Lista de medicamentos disponibles
            Text("Medicamentos Disponibles", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(medicamentosDisponibles) { medicamento ->
                    MedicamentoDisponibleCard(
                        medicamento = medicamento,
                        onAddClick = { selectedMedicamento ->
                            if (medicamentosNombresUsuario.contains(selectedMedicamento["nombre"])) {
                                errorMessage.value = "Ya has añadido este medicamento."
                            } else {
                                val db = FirebaseFirestore.getInstance()
                                val nuevoMedicamento = mapOf(
                                    "nombre" to selectedMedicamento["nombre"],
                                    "descripcion" to selectedMedicamento["descripcion"],
                                    "indicaciones" to selectedMedicamento["indicaciones"],
                                    "paciente_id" to userId
                                )
                                db.collection("medicamentos").add(nuevoMedicamento)
                                    .addOnSuccessListener { documentReference ->
                                        medicamentosUsuario.add(nuevoMedicamento + ("id" to documentReference.id))
                                        medicamentosNombresUsuario.add(selectedMedicamento["nombre"] ?: "")
                                    }
                                    .addOnFailureListener { e ->
                                        errorMessage.value = "Error al añadir medicamento: ${e.message}"
                                    }
                            }
                        }
                    )
                }
            }
        }
    }
}

/**
 * Tarjeta para los medicamentos del usuario.
 */
@Composable
fun MedicamentoUsuarioCard(
    medicamento: Map<String, String?>,
    onDeleteClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Nombre: ${medicamento["nombre"]}", style = MaterialTheme.typography.bodyLarge)
            Text("Descripción: ${medicamento["descripcion"]}", style = MaterialTheme.typography.bodyMedium)
            Text("Indicaciones: ${medicamento["indicaciones"]}", style = MaterialTheme.typography.bodyMedium)
            Button(
                onClick = { onDeleteClick(medicamento["id"] ?: "") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Eliminar")
            }
        }
    }
}

/**
 * Tarjeta para los medicamentos disponibles.
 */
@Composable
fun MedicamentoDisponibleCard(
    medicamento: Map<String, String?>,
    onAddClick: (Map<String, String?>) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Nombre: ${medicamento["nombre"]}", style = MaterialTheme.typography.bodyLarge)
            Text("Descripción: ${medicamento["descripcion"]}", style = MaterialTheme.typography.bodyMedium)
            Text("Indicaciones: ${medicamento["indicaciones"]}", style = MaterialTheme.typography.bodyMedium)
            Button(
                onClick = { onAddClick(medicamento) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Añadir")
            }
        }
    }
}
