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

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    // Ejecutamos un delay para simular el tiempo de carga
    LaunchedEffect(Unit) {
        delay(3000) // 3 segundos de espera
        onTimeout() // Llamamos a la acción de navegación
    }

    // Diseño de la pantalla Splash
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Imagen del logo
        Image(
            painter = painterResource(id = R.drawable.cross_image),
            contentDescription = "Cruz SaludConnect",
            modifier = Modifier.size(75.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))


        // Título de la aplicación
        Text(
            text = "SaludConnect",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.Black
        )

// Subtítulo de la aplicación
        Text(
            text = "“Innovación en la gestión de salud a tu alcance”",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray
        )
    }
}
