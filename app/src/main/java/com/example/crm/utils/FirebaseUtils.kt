package com.example.crm.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

fun loginWithFirebase(
    email: String,
    password: String,
    onSuccess: (Map<String, String>) -> Unit,
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

                // Recuperar información del usuario desde Firestore
                firestore.collection("users").document(userId).get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            val userData = document.data as Map<String, String>
                            onSuccess(userData)
                        } else {
                            onError("No se encontraron datos del usuario.")
                        }
                    }
                    .addOnFailureListener {
                        onError(it.localizedMessage ?: "Error al recuperar datos.")
                    }
            } else {
                // Mensajes de error personalizados
                val error = when (task.exception?.message) {
                    "There is no user record corresponding to this identifier." ->
                        "No existe una cuenta con este correo."
                    "The password is invalid or the user does not have a password." ->
                        "Contraseña incorrecta."
                    "A network error (such as timeout, interrupted connection or unreachable host) has occurred." ->
                        "Error de red. Por favor, intenta de nuevo."
                    else -> "Error desconocido. Por favor, intenta más tarde."
                }
                onError(error)
            }
        }
}

fun registerWithFirebase(
    email: String,
    password: String,
    name: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    if (email.isBlank() || password.isBlank() || name.isBlank()) {
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
                    "name" to name,
                    "email" to email,
                    "role" to "usuario" // Rol predeterminado
                )

                // Guardar datos en Firestore
                firestore.collection("users")
                    .document(userId)
                    .set(userData)
                    .addOnCompleteListener { dbTask ->
                        if (dbTask.isSuccessful) {
                            onSuccess() // Redirige automáticamente al inicio de sesión
                        } else {
                            onError(dbTask.exception?.localizedMessage ?: "Error al guardar datos.")
                        }
                    }
            } else {
                // Mensajes de error personalizados
                val error = when (task.exception?.message) {
                    "The email address is already in use by another account." ->
                        "El correo ya está en uso."
                    "The given password is invalid. [ Password should be at least 6 characters ]" ->
                        "La contraseña debe tener al menos 6 caracteres."
                    "A network error (such as timeout, interrupted connection or unreachable host) has occurred." ->
                        "Error de red. Por favor, intenta de nuevo."
                    else -> "Error desconocido. Por favor, intenta más tarde."
                }
                onError(error)
            }
        }
}

fun logout() {
    FirebaseAuth.getInstance().signOut()
}
