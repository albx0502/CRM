package com.example.crm.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.crm.R
import com.example.crm.utils.loginWithFirebase

/**
 * **LoginScreen**
 *
 * Pantalla de inicio de sesión que permite a los usuarios autenticarse.
 * Incluye campos de entrada para el correo y la contraseña, enlaces para
 * registro y recuperación de contraseña, y soporte para inicio de sesión con redes sociales.
 */
@Composable
fun LoginScreen(
    onLoginSuccess: (Map<String, Any>) -> Unit, // Callback para éxito en la autenticación
    onRegisterClick: () -> Unit, // Callback para ir a la pantalla de registro
    onForgotPasswordClick: () -> Unit // Callback para ir a la pantalla de recuperación de contraseña
) {
    // Estados para la entrada de datos y mensajes
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) } // Estado para alternar visibilidad de la contraseña

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo de la aplicación
        Image(
            painter = painterResource(id = R.drawable.cross_image),
            contentDescription = "Logo",
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Título de la pantalla
        Text(
            text = "Iniciar Sesión",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Campo de texto para el correo
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo Electrónico") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de texto para la contraseña
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (passwordVisible) R.drawable.ic_visibility_off else R.drawable.ic_visibility
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                        tint = Color.Unspecified // No aplica ningún color
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botón de inicio de sesión
        Button(
            onClick = {
                if (email.isNotBlank() && password.isNotBlank()) {
                    loginWithFirebase(
                        email,
                        password,
                        onSuccess = { userData -> onLoginSuccess(userData) },
                        onError = { errorMessage = it }
                    )
                } else {
                    errorMessage = "Por favor, completa ambos campos."
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(
                text = "Iniciar Sesión",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar mensaje de error si ocurre
        errorMessage?.let {
            Text(
                text = it,
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Enlaces de registro y recuperación de contraseña
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "¿No tienes cuenta? Regístrate.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onRegisterClick() }
            )
            Text(
                text = "¿Olvidaste tu contraseña?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onForgotPasswordClick() }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sección de redes sociales (placeholder)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SocialLoginIcon(R.drawable.ic_facebook, "Facebook")
            SocialLoginIcon(R.drawable.ic_google, "Google")
            SocialLoginIcon(R.drawable.ic_instagram, "Instagram")
        }
    }
}

/**
 * **SocialLoginIcon**
 *
 * Icono para representar redes sociales en el inicio de sesión.
 */
@Composable
fun SocialLoginIcon(iconResId: Int, description: String) {
    IconButton(onClick = { /* Acción de inicio de sesión social */ }) {
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = description,
            modifier = Modifier.size(48.dp)
        )
    }
}
