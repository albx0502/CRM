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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationsScreen(navController: NavController) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    val medicamentosDisponibles = remember { mutableStateListOf<Map<String, String?>>() }
    val medicamentosUsuario = remember { mutableStateListOf<Map<String, String?>>() }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val medicamentosNombresUsuario = remember { mutableStateListOf<String>() } // Almacena los nombres de medicamentos para evitar duplicados

    // Cargar medicamentos disponibles y del usuario desde Firestore
    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("medicamentos_disponibles")
            .get()
            .addOnSuccessListener { result ->
                medicamentosDisponibles.clear()
                for (document in result) {
                    medicamentosDisponibles.add(
                        mapOf(
                            "id" to document.id,
                            "nombre" to document.getString("nombre"),
                            "descripcion" to document.getString("descripcion"),
                            "indicaciones" to document.getString("indicaciones")
                        )
                    )
                }
            }
            .addOnFailureListener { e ->
                errorMessage.value = "Error al cargar medicamentos disponibles: ${e.message}"
            }

        db.collection("medicamentos")
            .whereEqualTo("paciente_id", userId)
            .get()
            .addOnSuccessListener { result ->
                medicamentosUsuario.clear()
                medicamentosNombresUsuario.clear()
                for (document in result) {
                    val nombre = document.getString("nombre") ?: "Desconocido"
                    medicamentosUsuario.add(
                        mapOf(
                            "id" to document.id,
                            "nombre" to nombre,
                            "descripcion" to document.getString("descripcion"),
                            "indicaciones" to document.getString("indicaciones")
                        )
                    )
                    medicamentosNombresUsuario.add(nombre)
                }
            }
            .addOnFailureListener { e ->
                errorMessage.value = "Error al cargar tus medicamentos: ${e.message}"
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Medicamentos Recetados") })
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (errorMessage.value != null) {
                // Mostrar el mensaje de error en un texto visible pero no bloquear la pantalla
                Text(
                    text = errorMessage.value ?: "Error desconocido",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Mostrar medicamentos del usuario
            Text("Tus Medicamentos", style = MaterialTheme.typography.headlineSmall)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                LazyColumn {
                    items(medicamentosUsuario) { medicamento ->
                        MedicamentoUsuarioCard(
                            medicamento = medicamento,
                            onDeleteClick = { medicamentoId ->
                                val db = FirebaseFirestore.getInstance()
                                db.collection("medicamentos").document(medicamentoId)
                                    .delete()
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
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mostrar medicamentos disponibles
            Text("Medicamentos Disponibles", style = MaterialTheme.typography.headlineSmall)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                LazyColumn {
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
                                    db.collection("medicamentos")
                                        .add(nuevoMedicamento)
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
}



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
            Text(text = "Nombre: ${medicamento["nombre"]}", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Descripción: ${medicamento["descripcion"]}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Indicaciones: ${medicamento["indicaciones"]}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { onDeleteClick(medicamento["id"] ?: "") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
            ) {
                Text("Eliminar")
            }
        }
    }
}

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
            Text(text = "Nombre: ${medicamento["nombre"]}", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Descripción: ${medicamento["descripcion"]}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Indicaciones: ${medicamento["indicaciones"]}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { onAddClick(medicamento) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Añadir")
            }
        }
    }
}
