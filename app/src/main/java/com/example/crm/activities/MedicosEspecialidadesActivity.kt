package com.example.crm.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.crm.components.SimpleDropdownSelector
import com.google.firebase.firestore.FirebaseFirestore

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
    val medicos = remember { mutableStateListOf<Map<String, Any>>() }
    val especialidades = remember { mutableStateListOf<Map<String, Any>>() }

    // Variables para crear un médico
    var nuevoMedicoNombre by remember { mutableStateOf("") }
    var nuevoMedicoApellidos by remember { mutableStateOf("") }
    var nuevoMedicoCorreo by remember { mutableStateOf("") }
    var especialidadSeleccionada by remember { mutableStateOf("") }

    // Cargar médicos y especialidades desde Firebase
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
            // Lista de médicos
            Text("Lista de Médicos", style = MaterialTheme.typography.headlineSmall)
            LazyColumn {
                items(medicos) { medico ->
                    Text("${medico["nombre"]} ${medico["apellidos"]} - ${medico["correo"]}")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Crear un médico
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

            Spacer(modifier = Modifier.height(16.dp))

            SimpleDropdownSelector(
                label = "Selecciona una especialidad",
                options = especialidades.map { it["nombre"].toString() }, // Mostrar nombres
                selectedOption = especialidades.firstOrNull { it["id"] == especialidadSeleccionada }?.get("nombre").toString(),
                onOptionSelected = { selectedName ->
                    especialidadSeleccionada = especialidades.firstOrNull { it["nombre"] == selectedName }?.get("id").toString()
                }
            )


            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                val nuevoMedico = mapOf(
                    "nombre" to nuevoMedicoNombre,
                    "apellidos" to nuevoMedicoApellidos,
                    "correo" to nuevoMedicoCorreo,
                    "especialidad_id" to especialidadSeleccionada
                )
                db.collection("medicos")
                    .add(nuevoMedico)
                    .addOnSuccessListener {
                        medicos.add(nuevoMedico)
                        nuevoMedicoNombre = ""
                        nuevoMedicoApellidos = ""
                        nuevoMedicoCorreo = ""
                        especialidadSeleccionada = ""
                        println("Médico creado exitosamente.")
                    }
                    .addOnFailureListener { e ->
                        println("Error al crear médico: ${e.message}")
                    }
            }) {
                Text("Añadir Médico")
            }
        }
    }
}
