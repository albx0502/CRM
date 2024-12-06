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
    val medicamentos = remember { mutableStateListOf<Map<String, String>>() }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    // Cargar medicamentos desde Firestore
    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("medicamentos")
            .whereEqualTo("paciente_id", userId)
            .get()
            .addOnSuccessListener { result ->
                medicamentos.clear()
                for (document in result) {
                    medicamentos.add(
                        mapOf(
                            "nombre" to (document.getString("nombre") ?: "Desconocido"),
                            "descripcion" to (document.getString("descripcion") ?: "Sin descripción"),
                            "indicaciones" to (document.getString("indicaciones") ?: "Sin indicaciones")
                        )
                    )
                }
            }
            .addOnFailureListener { e ->
                errorMessage.value = "Error al cargar medicamentos: ${e.message}"
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
                Text(
                    text = errorMessage.value ?: "Error desconocido",
                    color = MaterialTheme.colorScheme.error
                )
            } else if (medicamentos.isEmpty()) {
                Text(
                    text = "No tienes medicamentos recetados.",
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                LazyColumn {
                    items(medicamentos) { medicamento ->
                        MedicamentoCard(
                            nombre = medicamento["nombre"] ?: "Desconocido",
                            descripcion = medicamento["descripcion"] ?: "Sin descripción",
                            indicaciones = medicamento["indicaciones"] ?: "Sin indicaciones"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MedicamentoCard(nombre: String, descripcion: String, indicaciones: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Nombre: $nombre", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Descripción: $descripcion", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Indicaciones: $indicaciones", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
