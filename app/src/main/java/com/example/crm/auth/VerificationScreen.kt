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

@Composable
fun VerificationScreen(
    onResendClick: () -> Unit,
    onCreateNewPasswordClick: () -> Unit
) {
    // Estado para controlar el tiempo de espera antes de reenviar
    var isWaiting by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

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
                .wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Imagen en la parte superior
            Image(
                painter = painterResource(id = R.drawable.ic_verification_image), // Asegúrate de tener este recurso
                contentDescription = "Imagen de verificación",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(bottom = 16.dp)
            )

            // Mensaje de código de verificación
            Text(
                text = "Hemos enviado un código de verificación a --***24",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF001F54),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtítulo para indicar opciones en caso de que no se reciba el enlace
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
                        onResendClick()
                        isWaiting = true
                        coroutineScope.launch {
                            delay(30000) // Espera 30 segundos antes de permitir otro reenvío
                            isWaiting = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = !isWaiting // Deshabilitar el botón si está esperando
            ) {
                Text(
                    text = if (isWaiting) "Espera 30s para reenviar" else "Reenviar contraseña",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para Crear Nueva Contraseña
            Button(
                onClick = {
                    onCreateNewPasswordClick()
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
