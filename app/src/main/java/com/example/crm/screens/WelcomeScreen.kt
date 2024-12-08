package com.example.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.crm.R

/**
 * **WelcomeScreen**
 *
 * Esta pantalla es la pantalla de bienvenida que se muestra después de la **SplashScreen**.
 *
 * **Funciones principales:**
 * - Presentar el logo, título, subtítulo y una imagen de bienvenida.
 * - Mostrar dos botones principales:
 *    - **Registrarse** para crear una nueva cuenta.
 *    - **Iniciar sesión** para los usuarios que ya tienen una cuenta.
 *
 * **Parámetros de entrada:**
 * - `onRegisterClick`: Acción a realizar cuando se pulsa el botón de "Registrarse".
 * - `onLoginClick`: Acción a realizar cuando se pulsa el botón de "Iniciar sesión".
 */
@Composable
fun WelcomeScreen(onRegisterClick: () -> Unit, onLoginClick: () -> Unit) {
    // 📐 Diseño de la pantalla principal
    Column(
        modifier = Modifier
            .fillMaxSize() // Ocupar todo el espacio disponible
            .padding(16.dp), // Espaciado de los bordes
        verticalArrangement = Arrangement.Center, // Centrar verticalmente el contenido
        horizontalAlignment = Alignment.CenterHorizontally // Centrar horizontalmente el contenido
    ) {
        // 🔥 Logo principal de la aplicación
        Image(
            painter = painterResource(id = R.drawable.cross_image), // Recurso de la imagen (debe estar en la carpeta drawable)
            contentDescription = "Logo SaludConnect", // Descripción para accesibilidad
            modifier = Modifier
                .size(120.dp) // Tamaño de la imagen (se puede ajustar)
                .shadow(8.dp, RoundedCornerShape(12.dp)) // Sombra para dar profundidad
        )

        Spacer(modifier = Modifier.height(16.dp)) // Espacio entre el logo y el texto de bienvenida

        // 📝 Título principal
        Text(
            text = "Bienvenido", // Título de la pantalla
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), // Texto con negrita
            color = Color(0xFF001F54) // Color azul oscuro (personalizable)
        )

        Spacer(modifier = Modifier.height(8.dp)) // Espacio entre el título y el subtítulo

        // 📝 Subtítulo o mensaje de introducción
        Text(
            text = "“Gestión integral de pacientes y citas”.", // Subtítulo
            style = MaterialTheme.typography.bodyMedium, // Estilo de texto
            color = Color.Gray // Color gris para el subtítulo
        )

        Spacer(modifier = Modifier.height(24.dp)) // Espacio entre el subtítulo y la imagen principal

        // 📷 Imagen principal de bienvenida
        Image(
            painter = painterResource(id = R.drawable.welcome_image), // Recurso de la imagen (debe estar en la carpeta drawable)
            contentDescription = "Imagen de bienvenida", // Descripción para accesibilidad
            modifier = Modifier
                .fillMaxWidth() // Ocupar todo el ancho disponible
                .height(200.dp) // Altura de la imagen
                .shadow(8.dp, RoundedCornerShape(12.dp)) // Sombra alrededor de la imagen
        )

        Spacer(modifier = Modifier.height(24.dp)) // Espacio entre la imagen y el botón de registro

        // 🚀 **Botón de registro**
        Button(
            onClick = { onRegisterClick() }, // Acción al hacer clic (navegar a la pantalla de registro)
            modifier = Modifier
                .fillMaxWidth() // Ocupar todo el ancho
                .padding(horizontal = 16.dp) // Margen horizontal
                .height(48.dp) // Altura del botón
                .shadow(6.dp, RoundedCornerShape(12.dp)), // Borde redondeado y sombra
            shape = RoundedCornerShape(12.dp), // Bordes redondeados
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6A1B9A) // Color sólido para el botón
            )
        ) {
            Text(
                text = "Registrarse", // Texto del botón
                color = Color.White, // Color del texto
                fontSize = 16.sp, // Tamaño de la fuente
                fontWeight = FontWeight.Bold // Texto en negrita
            )
        }

        Spacer(modifier = Modifier.height(16.dp)) // Espacio entre el botón de registro y el texto de inicio de sesión

        // 📋 Texto para iniciar sesión (enlace clicable)
        Text(
            text = "¿Ya tienes cuenta? Iniciar Sesión", // Mensaje de inicio de sesión
            style = MaterialTheme.typography.bodyMedium, // Estilo del texto
            color = MaterialTheme.colorScheme.primary, // Color principal de la app (personalizable)
            modifier = Modifier
                .padding(top = 16.dp) // Espaciado superior
                .clickable { onLoginClick() } // Acción de clic (navegar a la pantalla de inicio de sesión)
        )
    }
}
