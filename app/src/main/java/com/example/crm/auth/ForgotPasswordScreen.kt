package com.example.crm.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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

@Composable
fun ForgotPasswordScreen(
    onSendLinkClick: () -> Unit,
    onBackClick: () -> Unit
) {
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
        // Caja contenedora para la parte superior con el ícono de retroceso y el logo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            // Botón de retroceso (esquina izquierda)
            Image(
                painter = painterResource(id = R.drawable.ic_back), // Icono de retroceso
                contentDescription = "Atrás",
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.CenterStart)
                    .clickable { onBackClick() }
            )

            // Logo de la cruz (centrado)
            Image(
                painter = painterResource(id = R.drawable.cross_image), // La cruz
                contentDescription = "Logo de SaludConnect",
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Título de la pantalla
        Text(
            text = "Olvidaste tu contraseña",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = Color(0xFF001F54),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Subtítulo de la pantalla
        Text(
            text = "Ingresa tu correo electrónico para restablecer tu contraseña.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Campo de entrada para el correo electrónico
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Mensajes de éxito o error
        if (successMessage != null) {
            Text(
                text = successMessage!!,
                color = Color(0xFF4CAF50), // Verde para éxito
                modifier = Modifier.padding(horizontal = 16.dp),
                textAlign = TextAlign.Center
            )
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = Color(0xFFF44336), // Rojo para error
                modifier = Modifier.padding(horizontal = 16.dp),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Botón de enviar enlace
        Button(
            onClick = {
                if (email.isNotBlank()) {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                successMessage = "Correo de recuperación enviado. Revisa tu bandeja."
                                errorMessage = null
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
                .height(48.dp)
        ) {
            Text(
                text = "Enviar enlace",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón de volver al inicio de sesión
        TextButton(onClick = onBackClick) {
            Text("Volver al inicio de sesión", color = Color(0xFF001F54))
        }
    }
}
