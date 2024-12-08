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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.crm.R
import com.google.firebase.auth.FirebaseAuth

/**
 * **ForgotPasswordScreen**
 *
 * Pantalla de recuperación de contraseña que permite a los usuarios solicitar
 * un enlace de restablecimiento de contraseña. Incluye:
 * - Campo de entrada para correo electrónico.
 * - Botón para enviar el enlace.
 * - Mensajes de error y éxito.
 * - Botón para volver a la pantalla de inicio de sesión.
 */
@Composable
fun ForgotPasswordScreen(
    onSendLinkClick: () -> Unit, // Callback para redirigir después de enviar el enlace
    onBackClick: () -> Unit // Callback para volver al inicio de sesión
) {
    // Estados locales para manejar entrada de correo y mensajes
    var email by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 🔙 Icono de retroceso y logo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            // Icono de retroceso
            Image(
                painter = painterResource(id = R.drawable.ic_back), // Icono en la carpeta drawable
                contentDescription = "Atrás",
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.CenterStart)
                    .clickable { onBackClick() }
            )

            // Logo centrado
            Image(
                painter = painterResource(id = R.drawable.cross_image),
                contentDescription = "Logo de SaludConnect",
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 📝 Título
        Text(
            text = "Olvidaste tu contraseña",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 📝 Subtítulo
        Text(
            text = "Ingresa tu correo electrónico para restablecer tu contraseña.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // ✉️ Campo de entrada para correo
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 🚦 Mensajes de éxito o error
        successMessage?.let {
            Text(
                text = it,
                color = Color(0xFF4CAF50), // Verde para éxito
                modifier = Modifier.padding(horizontal = 16.dp),
                textAlign = TextAlign.Center
            )
        }

        errorMessage?.let {
            Text(
                text = it,
                color = Color(0xFFF44336), // Rojo para error
                modifier = Modifier.padding(horizontal = 16.dp),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 🔗 Botón para enviar enlace
        Button(
            onClick = {
                if (email.isNotBlank()) {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                successMessage = "Correo de recuperación enviado. Revisa tu bandeja."
                                errorMessage = null
                                onSendLinkClick()
                            } else {
                                errorMessage = task.exception?.localizedMessage ?: "Error desconocido."
                                successMessage = null
                            }
                        }
                } else {
                    errorMessage = "El correo no puede estar vacío."
                    successMessage = null
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = "Enviar enlace",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 🔙 Botón para volver al inicio de sesión
        TextButton(onClick = onBackClick) {
            Text("Volver al inicio de sesión", color = MaterialTheme.colorScheme.primary)
        }
    }
}
