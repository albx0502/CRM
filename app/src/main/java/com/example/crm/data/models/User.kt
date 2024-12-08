package com.example.crm.data.models

import com.example.crm.R

/**
 * **Data Class: User**
 *
 * Esta clase representa un modelo de usuario que podría usarse en la aplicación.
 * Aunque actualmente no se está utilizando, se deja como **placeholder** por si en
 * el futuro se necesita trabajar con usuarios de forma más estructurada.
 *
 * **Atributos:**
 * - `name`: Nombre del usuario.
 * - `username`: Nombre de usuario (puede ser diferente del nombre completo).
 * - `email`: Correo electrónico del usuario.
 * - `profileImage`: Imagen de perfil (por defecto, se usa una imagen predeterminada `ic_profile_avatar`).
 *
 * **Posible uso futuro:**
 * - Si se requiere almacenar usuarios en la memoria de la app (caché local).
 * - Si se quiere cargar imágenes de perfil personalizadas.
 * - Usar esta clase para un sistema de autenticación personalizado.
 *
 * **Ejemplo de uso:**
 * ```kotlin
 * val usuarioEjemplo = User(
 *     name = "Juan Pérez",
 *     username = "jperez",
 *     email = "jperez@gmail.com"
 * )
 * ```
 */
data class User(
    val name: String, // Nombre completo del usuario
    val username: String, // Nombre de usuario (puede ser un alias o nombre de usuario único)
    val email: String, // Correo electrónico
    val profileImage: Int = R.drawable.ic_profile_avatar // Imagen de perfil (se usa una imagen predeterminada)
)
