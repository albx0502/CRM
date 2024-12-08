package com.example.crm.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * **CustomButton**
 *
 * Este componente representa un botón personalizado reutilizable con opciones de personalización
 * para el texto, colores, tamaño de fuente y más.
 *
 * **⚠️ NOTA:**
 * Actualmente, **NO SE UTILIZA** en la aplicación principal,
 * pero se ha mantenido como un **placeholder** por si en el futuro se requiere un botón reutilizable.
 *
 * **Ventajas de usar este componente:**
 * - Centralizar el diseño de los botones en la app.
 * - Permitir cambios rápidos de estilo sin modificar cada botón individual.
 * - Reutilización en varias pantallas con personalización de texto, color, etc.
 *
 * **Posibles mejoras futuras:**
 * - Usar iconos personalizados dentro del botón.
 * - Hacer que el tamaño del botón sea dinámico.
 *
 * @param text El texto que se mostrará en el botón.
 * @param onClick Acción que se ejecutará al hacer clic en el botón.
 * @param modifier Modificador que permite ajustar el tamaño, el margen y otras propiedades del botón.
 * @param backgroundColor Color de fondo del botón (por defecto: Azul oscuro #001F54).
 * @param textColor Color del texto dentro del botón (por defecto: Blanco).
 * @param fontSize Tamaño de la fuente (por defecto: 16sp).
 */
@Composable
fun CustomButton(
    text: String, // Texto que se mostrará en el botón.
    onClick: () -> Unit, // Acción a ejecutar cuando se hace clic en el botón.
    modifier: Modifier = Modifier, // Modificador opcional para personalizar la posición y tamaño.
    backgroundColor: Color = Color(0xFF001F54), // Color de fondo del botón (por defecto: Azul oscuro).
    textColor: Color = Color.White, // Color del texto (por defecto: Blanco).
    fontSize: Float = 16f // Tamaño de la fuente del texto (por defecto: 16sp).
) {
    Button(
        onClick = onClick, // Acción que se ejecutará cuando el usuario haga clic.
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor), // Colores personalizados.
        modifier = modifier
            .fillMaxWidth() // Ocupa todo el ancho de la pantalla.
            .height(48.dp) // Altura estándar del botón (48dp).
    ) {
        Text(
            text = text, // El texto que se mostrará en el botón.
            color = textColor, // Color del texto.
            fontSize = fontSize.sp, // Tamaño de la fuente.
            fontWeight = FontWeight.Bold // Peso de la fuente en negrita.
        )
    }
}
