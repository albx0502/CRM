package com.example.crm.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Pantalla de verificación que muestra un mensaje de verificación con dos opciones:
 * 1. Reenviar el código de verificación.
 * 2. Crear una nueva contraseña.
 *
 * Esta pantalla se podría utilizar en casos donde el usuario necesite verificar su cuenta
 * o confirmar un código de verificación, por ejemplo, tras una solicitud de recuperación de contraseña.
 *
 * @param onResendClick Callback que se ejecuta cuando se hace clic en "Reenviar contraseña".
 * @param onCreateNewPasswordClick Callback que se ejecuta cuando se hace clic en "Crear nueva contraseña".
 */
@Composable
fun VerificationScreen(
    onResendClick: () -> Unit,
    onCreateNewPasswordClick: () -> Unit
) {
    // Estado que controla si el botón de "Reenviar contraseña" está deshabilitado
    var isWaiting by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Caja principal que contiene todo el contenido
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .wrapContentHeight(), // Ajustar la altura según el contenido
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Imagen de verificación en la parte superior
            Image(
                painter = painterResource(id = R.drawable.ic_verification_image), // Reemplazar con la imagen correcta
                contentDescription = "Imagen de verificación",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(bottom = 16.dp)
            )

            // Título de verificación
            Text(
                text = "Hemos enviado un código de verificación a --***24",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF001F54), // Azul oscuro
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Texto explicativo para reenviar el código de verificación
            Text(
                text = "¿No recibiste el enlace? ¡Reenvía la contraseña a continuación!",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botón de Reenviar Contraseña
            Button(
                onClick = {
                    if (!isWaiting) {
                        onResendClick() // Llamada al callback para reenviar la contraseña
                        isWaiting = true // Deshabilitar el botón mientras se espera
                        coroutineScope.launch {
                            delay(30000) // Esperar 30 segundos antes de permitir otro reenvío
                            isWaiting = false // Rehabilitar el botón
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = !isWaiting // El botón se deshabilita mientras está en espera
            ) {
                Text(
                    text = if (isWaiting) "Espera 30s para reenviar" else "Reenviar contraseña",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para crear una nueva contraseña
            Button(
                onClick = {
                    onCreateNewPasswordClick() // Llamada al callback para crear una nueva contraseña
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = "Crear nueva contraseña",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
