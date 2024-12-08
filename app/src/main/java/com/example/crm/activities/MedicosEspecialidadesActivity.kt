package com.example.crm.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.crm.components.SimpleDropdownSelector
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * **MedicosEspecialidadesActivity**
 *
 * Actividad que gestiona la lista de médicos y especialidades.
 */
class MedicosEspecialidadesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MedicosEspecialidadesScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicosEspecialidadesScreen() {
    val db = FirebaseFirestore.getInstance()

    // Listas dinámicas
    val medicos = remember { mutableStateListOf<Map<String, Any>>() }
    val especialidades = remember { mutableStateListOf<Map<String, Any>>() }

    // Campos para crear un nuevo médico
    var nuevoMedicoNombre by remember { mutableStateOf("") }
    var nuevoMedicoApellidos by remember { mutableStateOf("") }
    var nuevoMedicoCorreo by remember { mutableStateOf("") }
    var especialidadSeleccionada by remember { mutableStateOf<String?>(null) }

    // Carga inicial de datos
    LaunchedEffect(Unit) {
        try {
            // Cargar médicos
            val medicosResult = db.collection("medicos").get().await()
            medicos.clear()
            medicos.addAll(medicosResult.documents.map { it.data.orEmpty() + ("id" to it.id) })

            // Cargar especialidades
            val especialidadesResult = db.collection("especialidades").get().await()
            especialidades.clear()
            especialidades.addAll(especialidadesResult.documents.map { it.data.orEmpty() + ("id" to it.id) })
        } catch (e: Exception) {
            println("Error al cargar datos: ${e.message}")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Médicos y Especialidades") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Lista de médicos
            Text("Lista de Médicos", style = MaterialTheme.typography.headlineSmall)
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp)
            ) {
                items(medicos) { medico ->
                    MedicoCard(medico)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Formulario para crear un nuevo médico
            Text("Crear Médico", style = MaterialTheme.typography.headlineSmall)
            OutlinedTextField(
                value = nuevoMedicoNombre,
                onValueChange = { nuevoMedicoNombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = nuevoMedicoApellidos,
                onValueChange = { nuevoMedicoApellidos = it },
                label = { Text("Apellidos") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = nuevoMedicoCorreo,
                onValueChange = { nuevoMedicoCorreo = it },
                label = { Text("Correo") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Selector de especialidad
            SimpleDropdownSelector(
                label = "Selecciona una especialidad",
                options = especialidades.map { it["nombre"].toString() },
                selectedOption = especialidades.firstOrNull { it["id"] == especialidadSeleccionada }?.get("nombre")?.toString()
                    ?: "Seleccionar",
                onOptionSelected = { selectedName ->
                    especialidadSeleccionada = especialidades.firstOrNull { it["nombre"] == selectedName }?.get("id") as String?
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para añadir un médico
            Button(
                onClick = {
                    if (nuevoMedicoNombre.isNotBlank() &&
                        nuevoMedicoApellidos.isNotBlank() &&
                        nuevoMedicoCorreo.isNotBlank() &&
                        !especialidadSeleccionada.isNullOrEmpty()
                    ) {
                        val nuevoMedico = mapOf(
                            "nombre" to nuevoMedicoNombre,
                            "apellidos" to nuevoMedicoApellidos,
                            "correo" to nuevoMedicoCorreo,
                            "especialidad_id" to especialidadSeleccionada!!
                        )

                        db.collection("medicos")
                            .add(nuevoMedico)
                            .addOnSuccessListener {
                                medicos.add(nuevoMedico + ("id" to it.id))
                                nuevoMedicoNombre = ""
                                nuevoMedicoApellidos = ""
                                nuevoMedicoCorreo = ""
                                especialidadSeleccionada = null
                                println("Médico creado exitosamente.")
                            }
                            .addOnFailureListener { e ->
                                println("Error al crear médico: ${e.message}")
                            }
                    } else {
                        println("Todos los campos son obligatorios.")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Añadir Médico", fontSize = 16.sp)
            }
        }
    }
}

/**
 * Componente para mostrar información de un médico en la lista.
 */
@Composable
fun MedicoCard(medico: Map<String, Any>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Nombre: ${medico["nombre"] ?: "Desconocido"}", style = MaterialTheme.typography.bodyLarge)
            Text("Apellidos: ${medico["apellidos"] ?: "Desconocido"}", style = MaterialTheme.typography.bodyLarge)
            Text("Correo: ${medico["correo"] ?: "No disponible"}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
