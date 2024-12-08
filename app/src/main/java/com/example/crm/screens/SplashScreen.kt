package com.example.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.example.crm.R

/**
 * **SplashScreen**
 *
 * Esta pantalla es la pantalla de carga inicial que se muestra antes de que la aplicación navegue a la pantalla de bienvenida o inicio de sesión.
 *
 * **Funciones principales:**
 * - Muestra una imagen del logo, el título y el subtítulo de la aplicación.
 * - Realiza un `delay` de 750ms (ajustable) antes de navegar a la siguiente pantalla.
 *
 * **Parámetros de entrada:**
 * - `onTimeout`: Callback que se ejecuta cuando se completa el tiempo de carga, generalmente para navegar a la siguiente pantalla.
 */
@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    // ⚙️ Ejecutar una acción después de un retraso de 750 ms
    LaunchedEffect(Unit) {
        delay(750) // Espera de 750 ms (se puede ajustar si se quiere un splash más largo)
        onTimeout() // Llamada a la acción de navegación
    }

    // 📐 Diseño de la pantalla Splash
    Column(
        modifier = Modifier
            .fillMaxSize() // Ocupar todo el espacio de la pantalla
            .padding(16.dp), // Margen alrededor del contenido
        verticalArrangement = Arrangement.Center, // Centrar verticalmente el contenido
        horizontalAlignment = Alignment.CenterHorizontally // Centrar horizontalmente el contenido
    ) {
        // 🔥 Imagen del logo de la aplicación
        Image(
            painter = painterResource(id = R.drawable.cross_image), // El recurso de la imagen del logo
            contentDescription = "Logo SaludConnect", // Descripción para accesibilidad
            modifier = Modifier.size(100.dp) // Tamaño de la imagen (ajustar si se necesita más grande)
        )

        Spacer(modifier = Modifier.height(16.dp)) // Espacio entre la imagen y el título

        // 📝 Título de la aplicación
        Text(
            text = "SaludConnect", // Nombre de la aplicación
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), // Estilo de texto
            color = Color.Black // Color del texto
        )

        Spacer(modifier = Modifier.height(8.dp)) // Espacio entre el título y el subtítulo

        // 📘 Subtítulo de la aplicación
        Text(
            text = "“Innovación en la gestión de salud a tu alcance”", // Frase de presentación
            style = MaterialTheme.typography.bodyLarge, // Estilo de texto
            color = Color.Gray // Color del texto
        )
    }
}
