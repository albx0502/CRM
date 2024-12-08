package com.example.crm.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * **MedicalCheckupsScreen**
 *
 * Pantalla que muestra la lista de los chequeos médicos del usuario actual.
 * Se cargan desde la colección **"resultados"** en Firestore.
 *
 * **Funciones principales:**
 * - Ver la lista de resultados médicos con fecha y descripción.
 * - Descargar archivos PDF asociados a los resultados.
 *
 * **Requisitos:**
 * - Los datos se obtienen de la colección **"resultados"**.
 * - Cada resultado debe tener la clave `archivo_pdf` que contiene la URL del PDF.
 * - Si no hay archivo PDF disponible, se muestra un mensaje de error.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalCheckupsScreen(navController: NavController) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty() // ID del usuario autenticado
    val results = remember { mutableStateListOf<Map<String, Any>>() } // Lista de resultados médicos
    val context = LocalContext.current // Contexto de la app para lanzar intentos
    var isLoading by remember { mutableStateOf(true) } // Estado para mostrar un indicador de carga
    var errorMessage by remember { mutableStateOf<String?>(null) } // Mensaje de error si ocurre

    // 🔄 Cargar resultados médicos desde Firestore
    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        try {
            val result = db.collection("resultados")
                .whereEqualTo("paciente_id", userId) // Filtrar por usuario autenticado
                .get()
                .await()

            results.clear()
            for (doc in result) {
                val data = doc.data.toMutableMap()
                data["id"] = doc.id // Incluye el ID del documento
                results.add(data)
            }
            isLoading = false
        } catch (e: Exception) {
            errorMessage = "Error al cargar los resultados: ${e.message}"
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chequeos Médicos", fontSize = 20.sp, fontWeight = FontWeight.Bold) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (isLoading) {
                // Indicador de carga
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                )
            } else if (errorMessage != null) {
                // Mostrar mensaje de error
                Text(
                    text = errorMessage ?: "Error desconocido",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            } else if (results.isEmpty()) {
                // Mensaje de que no hay resultados
                Text(
                    text = "No se encontraron chequeos médicos.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            } else {
                // Lista de resultados médicos
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
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
    }
}

/**
 * **ResultCard**
 *
 * Tarjeta que muestra un resultado médico individual con la siguiente información:
 * - **Fecha**: La fecha en la que se realizó el chequeo médico.
 * - **Descripción**: Breve descripción del chequeo.
 * - **Botón de descarga de PDF**: Permite descargar el PDF del resultado.
 *
 * **Parámetros:**
 * - **result**: Mapa que contiene los datos del resultado.
 * - **onDownloadClick**: Acción que se ejecuta al hacer clic en "Descargar PDF".
 */
@Composable
fun ResultCard(result: Map<String, Any>, onDownloadClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp), // Espaciado entre tarjetas
        elevation = CardDefaults.cardElevation(6.dp) // Elevación para la sombra de la tarjeta
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 📅 Fecha del chequeo médico
            Text(
                text = "Fecha: ${result["fecha"] ?: "Sin fecha"}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )

            // 📝 Descripción del chequeo médico
            Text(
                text = "Descripción: ${result["descripcion"] ?: "Sin descripción"}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
            )

            // 📂 URL del PDF (si está disponible)
            val pdfUrl = result["archivo_pdf"] as? String
            if (!pdfUrl.isNullOrEmpty()) {
                // Botón para descargar el PDF
                Button(
                    onClick = { onDownloadClick(pdfUrl) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                ) {
                    Text("Descargar PDF")
                }
            } else {
                // Mensaje de error si no hay PDF
                Text(
                    text = "PDF no disponible",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
