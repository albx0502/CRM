package com.example.crm.data.models

data class User(
    val id: String = "", // UID del usuario en Firebase
    val name: String = "",
    val email: String = "",
    val role: String = "usuario", // Rol por defecto
    val profilePictureUrl: String? = null // Opcional para imágenes de perfil
)
