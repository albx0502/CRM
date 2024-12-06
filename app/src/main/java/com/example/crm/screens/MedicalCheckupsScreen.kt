package com.example.crm.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalCheckupsScreen(navController: NavController) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    val results = remember { mutableStateListOf<Map<String, Any>>() }
    val context = LocalContext.current

    // Cargar resultados médicos desde Firestore
    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("resultados")
            .whereEqualTo("paciente_id", userId)
            .get()
            .addOnSuccessListener { result ->
                results.clear()
                result.forEach { doc ->
                    val data = doc.data.toMutableMap()
                    data["id"] = doc.id // Incluye el ID del documento
                    results.add(data)
                }
            }
            .addOnFailureListener { e ->
                println("Error al cargar resultados: ${e.message}")
            }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Chequeos Médicos") }) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            items(results) { result ->
                ResultCard(result) { pdfUrl ->
                    // Abre el PDF en el navegador
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(pdfUrl))
                    context.startActivity(intent)
                }
            }
        }
    }
}

@Composable
fun ResultCard(result: Map<String, Any>, onDownloadClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Fecha: ${result["fecha"] ?: "Sin fecha"}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Descripción: ${result["descripcion"] ?: "Sin descripción"}",
                style = MaterialTheme.typography.bodyMedium
            )
            val pdfUrl = result["archivo_pdf"] as? String
            if (!pdfUrl.isNullOrEmpty()) {
                TextButton(onClick = { onDownloadClick(pdfUrl) }) {
                    Text("Descargar PDF")
                }
            } else {
                Text(
                    "PDF no disponible",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
