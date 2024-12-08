package com.example.crm.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.crm.components.BottomNavigationBar
import com.example.crm.utils.addCita
import com.example.crm.components.SimpleDropdownSelector
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * **SimuladorCitasScreen**
 *
 * Pantalla para simular la creación de citas médicas.
 */
@Composable
fun SimuladorCitasScreen(navController: NavController) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

    // Estados para los datos del formulario
    var fecha by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }
    var especialidadId by remember { mutableStateOf("") }
    var especialidadNombre by remember { mutableStateOf("") }
    var medicoId by remember { mutableStateOf("") }
    var medicoNombre by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Listas dinámicas para especialidades y médicos
    val especialidades = remember { mutableStateListOf<Pair<String, String>>() }
    val medicosFiltrados = remember { mutableStateListOf<Pair<String, String>>() }

    // 🚀 Cargar especialidades y médicos
    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()

        db.collection("especialidades")
            .get()
            .addOnSuccessListener { result ->
                especialidades.clear()
                especialidades.addAll(result.map { it.id to (it.getString("nombre") ?: "Desconocida") })
            }
            .addOnFailureListener { e -> errorMessage = "Error al cargar especialidades: ${e.message}" }

        db.collection("medicos")
            .get()
            .addOnSuccessListener { result ->
                medicosFiltrados.clear()
                medicosFiltrados.addAll(result.map { it.id to (it.getString("nombre") ?: "Desconocido") })
            }
            .addOnFailureListener { e -> errorMessage = "Error al cargar médicos: ${e.message}" }
    }

    LaunchedEffect(especialidadId) {
        if (especialidadId.isNotEmpty()) {
            val db = FirebaseFirestore.getInstance()

            db.collection("medicos")
                .whereEqualTo("especialidad_id", especialidadId)
                .get()
                .addOnSuccessListener { result ->
                    medicosFiltrados.clear()
                    medicosFiltrados.addAll(result.map { it.id to (it.getString("nombre") ?: "Desconocido") })
                }
                .addOnFailureListener { e -> errorMessage = "Error al filtrar médicos: ${e.message}" }
        }
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Simulador de Citas",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Fecha
            OutlinedTextField(
                value = fecha,
                onValueChange = { fecha = it },
                label = { Text("Fecha (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Hora
            OutlinedTextField(
                value = hora,
                onValueChange = { hora = it },
                label = { Text("Hora (HH:MM)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Selector de especialidad
            SimpleDropdownSelector(
                label = "Selecciona una especialidad",
                options = especialidades.map { it.second },
                selectedOption = especialidadNombre,
                onOptionSelected = { selected ->
                    especialidadNombre = selected
                    especialidadId = especialidades.firstOrNull { it.second == selected }?.first.orEmpty()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Selector de médico
            SimpleDropdownSelector(
                label = "Selecciona un médico",
                options = medicosFiltrados.map { it.second },
                selectedOption = medicoNombre,
                onOptionSelected = { selected ->
                    medicoNombre = selected
                    medicoId = medicosFiltrados.firstOrNull { it.second == selected }?.first.orEmpty()
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de Guardar Cita
            Button(
                onClick = {
                    if (fecha.isNotEmpty() && hora.isNotEmpty() && especialidadId.isNotEmpty() && medicoId.isNotEmpty()) {
                        addCita(
                            fecha = fecha,
                            hora = hora,
                            especialidadId = especialidadId,
                            medicoId = medicoId,
                            pacienteId = userId,
                            onSuccess = {
                                successMessage = "Cita creada con éxito."
                                errorMessage = null
                            },
                            onError = { errorMessage = it; successMessage = null }
                        )
                    } else {
                        errorMessage = "Por favor, completa todos los campos."
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Cita")
            }

            // Mensaje de éxito
            successMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            // Mensaje de error
            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}
