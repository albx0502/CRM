package com.example.crm.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * **Paleta de colores para tema oscuro**
 *
 * Define los colores principales, secundarios y terciarios para la interfaz en **modo oscuro**.
 */
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,         // Color principal (para botones, enlaces, etc.)
    secondary = PurpleGrey80,   // Color secundario (para énfasis o detalles)
    tertiary = Pink80           // Color terciario (para detalles de soporte)
)

/**
 * **Paleta de colores para tema claro**
 *
 * Define los colores principales, secundarios y terciarios para la interfaz en **modo claro**.
 */
private val LightColorScheme = lightColorScheme(
    primary = Purple40,         // Color principal (para botones, enlaces, etc.)
    secondary = PurpleGrey40,   // Color secundario (para énfasis o detalles)
    tertiary = Pink40           // Color terciario (para detalles de soporte)

    /* Otros colores predeterminados que se pueden sobrescribir
    background = Color(0xFFFFFBFE),  // Color de fondo
    surface = Color(0xFFFFFBFE),     // Color de la superficie (por ejemplo, tarjetas)
    onPrimary = Color.White,         // Color del contenido en elementos primarios
    onSecondary = Color.White,       // Color del contenido en elementos secundarios
    onTertiary = Color.White,        // Color del contenido en elementos terciarios
    onBackground = Color(0xFF1C1B1F),// Color del contenido en el fondo
    onSurface = Color(0xFF1C1B1F),   // Color del contenido en la superficie
    */
)

/**
 * **CRMTheme** - Componente de tema principal de la aplicación
 *
 * Esta función define el esquema de colores y la tipografía que se aplicará a toda la aplicación.
 *
 * **Parámetros:**
 * - `darkTheme`: (Boolean) Indica si se usa el modo oscuro o claro. Actualmente, se establece en **falso** para que la app esté siempre en modo claro.
 * - `content`: (Composable) El contenido que se verá envuelto por este tema.
 *
 * **Funcionamiento:**
 * - Se define la paleta de colores (colorScheme) que se utilizará en toda la aplicación.
 * - Actualmente, la opción `darkTheme` está deshabilitada, por lo que siempre se usa el tema claro.
 */
@Composable
fun CRMTheme(
    darkTheme: Boolean = false, // 🔦 Siempre estará en modo claro (para permitir modo oscuro, cambiar a "isSystemInDarkTheme()")
    content: @Composable () -> Unit // 🌐 Contenido composable que se verá afectado por este tema
) {
    // 🌈 Selecciona el esquema de color.
    // Nota: Se puede cambiar para detectar automáticamente si el sistema está en modo oscuro.
    val colorScheme = LightColorScheme // Se fija a "LightColorScheme" para usar siempre el tema claro

    // 📐 Aplica el tema de la aplicación
    MaterialTheme(
        colorScheme = colorScheme, // Esquema de colores definido previamente
        typography = Typography,  // Se define la tipografía (debe estar definida en un archivo Typography.kt)
        content = content // Se aplica el tema a todo el contenido hijo
    )
}
