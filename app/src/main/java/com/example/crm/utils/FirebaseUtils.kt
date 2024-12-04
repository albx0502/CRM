package com.example.crm.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

fun loginWithFirebase(
    email: String,
    password: String,
    onSuccess: (Map<String, Any>) -> Unit,
    onError: (String) -> Unit
) {
    if (email.isBlank() || password.isBlank()) {
        onError("El correo y la contraseña no pueden estar vacíos.")
        return
    }

    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = auth.currentUser?.uid ?: return@addOnCompleteListener

                // Recuperar datos del usuario desde Firestore
                firestore.collection("usuarios").document(userId).get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            val userData = document.data ?: emptyMap()
                            onSuccess(userData)
                        } else {
                            onError("No se encontraron datos del usuario.")
                        }
                    }
                    .addOnFailureListener {
                        onError(it.localizedMessage ?: "Error al recuperar datos.")
                    }
            } else {
                val error = task.exception?.localizedMessage ?: "Error desconocido."
                onError(error)
            }
        }
}

fun registerWithFirebase(
    email: String,
    password: String,
    nombre: String,
    apellidos: String,
    telefono: String,
    sexo: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    if (email.isBlank() || password.isBlank() || nombre.isBlank() || apellidos.isBlank() || telefono.isBlank() || sexo.isBlank()) {
        onError("Todos los campos son obligatorios.")
        return
    }

    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = task.result?.user?.uid ?: return@addOnCompleteListener
                val userData = mapOf(
                    "nombre" to nombre,
                    "apellidos" to apellidos,
                    "telefono" to telefono,
                    "sexo" to sexo,
                    "correo" to email,
                    "rol" to "paciente" // Rol predeterminado
                )

                // Guardar datos del usuario en Firestore
                firestore.collection("usuarios").document(userId).set(userData)
                    .addOnCompleteListener { dbTask ->
                        if (dbTask.isSuccessful) {
                            onSuccess()
                        } else {
                            onError(dbTask.exception?.localizedMessage ?: "Error al guardar datos.")
                        }
                    }
            } else {
                val error = task.exception?.localizedMessage ?: "Error desconocido."
                onError(error)
            }
        }
}

fun logout() {
    FirebaseAuth.getInstance().signOut()
}

// NUEVOS MÉTODOS PARA LAS NUEVAS COLECCIONES:

fun addUsuario(
    userId: String,
    nombre: String,
    apellidos: String,
    telefono: String,
    sexo: String,
    correo: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val firestore = FirebaseFirestore.getInstance()
    val userData = mapOf(
        "nombre" to nombre,
        "apellidos" to apellidos,
        "telefono" to telefono,
        "sexo" to sexo,
        "correo" to correo,
        "rol" to "paciente"
    )

    firestore.collection("usuarios")
        .document(userId)
        .set(userData)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { onError(it.localizedMessage ?: "Error al agregar usuario.") }
}

fun addMedico(
    medicoId: String,
    nombre: String,
    apellidos: String,
    telefono: String,
    correo: String,
    especialidadId: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val firestore = FirebaseFirestore.getInstance()
    val medicoData = mapOf(
        "nombre" to nombre,
        "apellidos" to apellidos,
        "telefono" to telefono,
        "correo" to correo,
        "especialidad_id" to especialidadId
    )

    firestore.collection("medicos")
        .document(medicoId)
        .set(medicoData)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { onError(it.localizedMessage ?: "Error al agregar médico.") }
}

fun addEspecialidad(
    especialidadId: String,
    nombre: String,
    descripcion: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val firestore = FirebaseFirestore.getInstance()
    val especialidadData = mapOf(
        "nombre" to nombre,
        "descripcion" to descripcion
    )

    firestore.collection("especialidades")
        .document(especialidadId)
        .set(especialidadData)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { onError(it.localizedMessage ?: "Error al agregar especialidad.") }
}

fun addCita(
    fecha: String,
    hora: String,
    especialidadId: String,
    medicoId: String,
    pacienteId: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val firestore = FirebaseFirestore.getInstance()
    val citaData = mapOf(
        "fecha" to fecha,
        "hora" to hora,
        "especialidad_id" to especialidadId,
        "medico_id" to medicoId,
        "paciente_id" to pacienteId
    )

    firestore.collection("citas")
        .add(citaData)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { exception ->
            onError(exception.localizedMessage ?: "Error al guardar la cita.")
        }
}


// Otros métodos para citas, medicamentos, resultados, etc. pueden agregarse de forma similar.
