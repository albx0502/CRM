package com.example.crm.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * **loginWithFirebase**
 *
 * Inicia sesión con correo y contraseña.
 *
 * **Parámetros:**
 * - `email`: Correo del usuario.
 * - `password`: Contraseña del usuario.
 * - `onSuccess`: Función de éxito, recibe los datos del usuario desde Firestore.
 * - `onError`: Función de error, recibe el mensaje de error.
 */
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

/**
 * **registerWithFirebase**
 *
 * Registra un usuario nuevo con correo y contraseña.
 *
 * **Parámetros:**
 * - `email`, `password`, `nombre`, `apellidos`, `telefono`, `sexo`: Datos del usuario.
 * - `onSuccess`: Función de éxito.
 * - `onError`: Función de error.
 */
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
                    "rol" to "paciente"
                )

                firestore.collection("usuarios").document(userId).set(userData)
                    .addOnCompleteListener { dbTask ->
                        if (dbTask.isSuccessful) onSuccess()
                        else onError(dbTask.exception?.localizedMessage ?: "Error al guardar datos.")
                    }
            } else {
                onError(task.exception?.localizedMessage ?: "Error desconocido.")
            }
        }
}

/**
 * **logout**
 *
 * Cierra la sesión actual del usuario.
 */
fun logout() {
    FirebaseAuth.getInstance().signOut()
}

/**
 * **addUsuario**
 *
 * Añade un nuevo usuario a Firestore.
 */
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

/**
 * **addMedico**
 *
 * Añade un nuevo médico a la colección "medicos".
 */
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

/**
 * **addEspecialidad**
 *
 * Añade una nueva especialidad médica.
 */
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

/**
 * **addCita**
 *
 * Crea una cita médica y genera automáticamente un resultado asociado.
 */
fun addCita(
    fecha: String,
    hora: String,
    especialidadId: String,
    medicoId: String,
    pacienteId: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val nuevaCita = hashMapOf(
        "fecha" to fecha,
        "hora" to hora,
        "especialidad_id" to especialidadId,
        "medico_id" to medicoId,
        "paciente_id" to pacienteId
    )

    db.collection("citas")
        .add(nuevaCita)
        .addOnSuccessListener { documentReference ->
            val citaId = documentReference.id

            val nuevoResultado = hashMapOf(
                "fecha" to fecha,
                "descripcion" to "Resultado generado automáticamente para la cita.",
                "cita_id" to citaId,
                "paciente_id" to pacienteId,
                "archivo_pdf" to "https://firebasestorage.googleapis.com/.../resultado_generico.pdf"
            )

            db.collection("resultados")
                .add(nuevoResultado)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { e ->
                    onError("Cita creada, pero error al generar resultado: ${e.message}")
                }
        }
        .addOnFailureListener { e ->
            onError("Error al crear cita: ${e.message}")
        }
}
