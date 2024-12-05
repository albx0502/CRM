package com.example.crm.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore

class MedicosEspecialidadesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MedicosEspecialidadesScreen()
        }
    }
}

@Composable
fun MedicosEspecialidadesScreen() {
    val db = FirebaseFirestore.getInstance()
    val medicos = remember { mutableStateListOf<Map<String, Any>>() }
    val especialidades = remember { mutableStateListOf<Map<String, Any>>() }

    // Variables para crear un médico o especialidad
    var nuevoMedicoNombre by remember { mutableStateOf("") }
    var nuevoMedicoApellidos by remember { mutableStateOf("") }
    var nuevoMedicoCorreo by remember { mutableStateOf("") }
    var nuevaEspecialidadNombre by remember { mutableStateOf("") }
    var nuevaEspecialidadDescripcion by remember { mutableStateOf("") }

    // Cargar médicos y especialidades desde Firebase
    LaunchedEffect(Unit) {
        db.collection("medicos")
            .get()
            .addOnSuccessListener { result ->
                medicos.clear()
                medicos.addAll(result.map { it.data })
            }
            .addOnFailureListener { e ->
                println("Error al cargar médicos: ${e.message}")
            }

        db.collection("especialidades")
            .get()
            .addOnSuccessListener { result ->
                especialidades.clear()
                especialidades.addAll(result.map { it.data })
            }
            .addOnFailureListener { e ->
                println("Error al cargar especialidades: ${e.message}")
            }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Médicos y Especialidades") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("Lista de Médicos", style = MaterialTheme.typography.headlineSmall)
            LazyColumn {
                items(medicos) { medico ->
                    Text("${medico["nombre"]} ${medico["apellidos"]} - ${medico["correo"]}")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Lista de Especialidades", style = MaterialTheme.typography.headlineSmall)
            LazyColumn {
                items(especialidades) { especialidad ->
                    Text("${especialidad["nombre"]} - ${especialidad["descripcion"]}")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Formulario para crear un médico
            Text("Crear Médico", style = MaterialTheme.typography.headlineSmall)
            OutlinedTextField(
                value = nuevoMedicoNombre,
                onValueChange = { nuevoMedicoNombre = it },
                label = { Text("Nombre") }
            )
            OutlinedTextField(
                value = nuevoMedicoApellidos,
                onValueChange = { nuevoMedicoApellidos = it },
                label = { Text("Apellidos") }
            )
            OutlinedTextField(
                value = nuevoMedicoCorreo,
                onValueChange = { nuevoMedicoCorreo = it },
                label = { Text("Correo") }
            )
            Button(onClick = {
                val nuevoMedico = mapOf(
                    "nombre" to nuevoMedicoNombre,
                    "apellidos" to nuevoMedicoApellidos,
                    "correo" to nuevoMedicoCorreo
                )
                db.collection("medicos")
                    .add(nuevoMedico)
                    .addOnSuccessListener {
                        medicos.add(nuevoMedico)
                        nuevoMedicoNombre = ""
                        nuevoMedicoApellidos = ""
                        nuevoMedicoCorreo = ""
                        println("Médico creado exitosamente.")
                    }
                    .addOnFailureListener { e ->
                        println("Error al crear médico: ${e.message}")
                    }
            }) {
                Text("Añadir Médico")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Formulario para crear una especialidad
            Text("Crear Especialidad", style = MaterialTheme.typography.headlineSmall)
            OutlinedTextField(
                value = nuevaEspecialidadNombre,
                onValueChange = { nuevaEspecialidadNombre = it },
                label = { Text("Nombre") }
            )
            OutlinedTextField(
                value = nuevaEspecialidadDescripcion,
                onValueChange = { nuevaEspecialidadDescripcion = it },
                label = { Text("Descripción") }
            )
            Button(onClick = {
                val nuevaEspecialidad = mapOf(
                    "nombre" to nuevaEspecialidadNombre,
                    "descripcion" to nuevaEspecialidadDescripcion
                )
                db.collection("especialidades")
                    .add(nuevaEspecialidad)
                    .addOnSuccessListener {
                        especialidades.add(nuevaEspecialidad)
                        nuevaEspecialidadNombre = ""
                        nuevaEspecialidadDescripcion = ""
                        println("Especialidad creada exitosamente.")
                    }
                    .addOnFailureListener { e ->
                        println("Error al crear especialidad: ${e.message}")
                    }
            }) {
                Text("Añadir Especialidad")
            }
        }
    }
}
