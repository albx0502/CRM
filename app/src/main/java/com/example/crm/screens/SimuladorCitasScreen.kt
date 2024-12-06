package com.example.crm.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.crm.components.BottomNavigationBar
import com.example.crm.utils.addCita
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.crm.components.SimpleDropdownSelector


@Composable
fun SimuladorCitasScreen(navController: NavController) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

    var fecha by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }
    var especialidadId by remember { mutableStateOf("") }
    var especialidadNombre by remember { mutableStateOf("") }
    var medicoId by remember { mutableStateOf("") }
    var medicoNombre by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Variables para cargar opciones dinámicamente
    val especialidades = remember { mutableStateListOf<Pair<String, String>>() }
    val medicos = remember { mutableStateListOf<Pair<String, String>>() }

    // Cargar datos desde Firebase
    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()

        db.collection("especialidades")
            .get()
            .addOnSuccessListener { result ->
                especialidades.clear()
                especialidades.addAll(result.map { it.id to (it.getString("nombre") ?: "Desconocida") })
            }
            .addOnFailureListener { e ->
                errorMessage = "Error al cargar especialidades: ${e.message}"
            }

        db.collection("medicos")
            .get()
            .addOnSuccessListener { result ->
                medicos.clear()
                medicos.addAll(result.map { it.id to (it.getString("nombre") ?: "Desconocido") })
            }
            .addOnFailureListener { e ->
                errorMessage = "Error al cargar médicos: ${e.message}"
            }
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Simulador de Citas", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = fecha,
                onValueChange = { fecha = it },
                label = { Text("Fecha (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = hora,
                onValueChange = { hora = it },
                label = { Text("Hora (HH:MM)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            SimpleDropdownSelector(
                label = "Selecciona una especialidad",
                options = especialidades.map { it.second }, // Mostrar nombres
                selectedOption = especialidadNombre,
                onOptionSelected = { selected ->
                    especialidadNombre = selected
                    especialidadId = especialidades.firstOrNull { it.second == selected }?.first.orEmpty()
                }
            )


            Spacer(modifier = Modifier.height(16.dp))

            SimpleDropdownSelector(
                label = "Selecciona un médico",
                options = medicos.map { it.second }, // Mostrar nombres
                selectedOption = medicoNombre,
                onOptionSelected = { selected ->
                    medicoNombre = selected
                    medicoId = medicos.firstOrNull { it.second == selected }?.first.orEmpty()
                }
            )


            Spacer(modifier = Modifier.height(16.dp))

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

            successMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 16.dp))
            }

            errorMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 16.dp))
            }
        }
    }
}


