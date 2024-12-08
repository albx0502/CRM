package com.example.crm.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize

/**
 * **SimpleDropdownSelector**
 *
 * Este componente crea un selector desplegable (Dropdown) simple y reutilizable.
 *
 * **⚠️ Nota:** Este selector permite al usuario elegir una opción de una lista.
 * Es completamente reutilizable y fácil de personalizar.
 *
 * **Parámetros:**
 * - `label`: El texto que se muestra como marcador de posición antes de seleccionar una opción.
 * - `options`: La lista de opciones que se mostrarán en el menú desplegable.
 * - `selectedOption`: La opción seleccionada actualmente.
 * - `onOptionSelected`: Callback que se ejecuta cuando el usuario selecciona una opción.
 *
 * **Mejoras Futuras:**
 * - Personalización de estilos para texto, color de fondo y tamaño.
 * - Soporte para íconos junto a las opciones.
 * - Mostrar una flecha o ícono al lado derecho para indicar que se puede desplegar.
 *
 * **Ejemplo de uso:**
 * ```
 * SimpleDropdownSelector(
 *     label = "Seleccionar especialidad",
 *     options = listOf("Cardiología", "Dermatología", "Odontología"),
 *     selectedOption = selectedEspecialidad,
 *     onOptionSelected = { nuevaSeleccion -> selectedEspecialidad = nuevaSeleccion }
 * )
 * ```
 *
 * @param label El texto que se muestra cuando no hay ninguna opción seleccionada.
 * @param options Lista de opciones que se pueden seleccionar.
 * @param selectedOption La opción seleccionada actualmente.
 * @param onOptionSelected Callback que se llama cuando se selecciona una opción.
 */
@Composable
fun SimpleDropdownSelector(
    label: String, // Texto que se muestra cuando no hay una opción seleccionada.
    options: List<String>, // Lista de opciones para mostrar en el menú desplegable.
    selectedOption: String, // La opción actualmente seleccionada.
    onOptionSelected: (String) -> Unit // Acción que se ejecuta cuando se selecciona una opción.
) {
    // Controla si el menú desplegable está abierto o cerrado
    var expanded by remember { mutableStateOf(false) }

    // Contenedor del selector
    Box(
        modifier = Modifier
            .fillMaxWidth() // Ocupa todo el ancho disponible
            .padding(8.dp) // Margen alrededor del selector
            .clickable { expanded = true } // Activa el menú al hacer clic en cualquier parte del cuadro
            .background(MaterialTheme.colorScheme.surface) // Color de fondo del cuadro
            .padding(16.dp) // Espaciado interno dentro del cuadro
    ) {
        // Texto que se muestra en el cuadro (opción seleccionada o etiqueta)
        Text(
            text = if (selectedOption.isEmpty()) label else selectedOption, // Muestra la opción seleccionada o la etiqueta
            style = MaterialTheme.typography.bodyLarge, // Estilo de texto
            color = if (selectedOption.isEmpty())
                MaterialTheme.colorScheme.onSurfaceVariant // Color de texto cuando no hay opción seleccionada
            else
                MaterialTheme.colorScheme.onSurface // Color de texto cuando hay una opción seleccionada
        )

        // Menú desplegable que aparece al hacer clic
        DropdownMenu(
            expanded = expanded, // Controla si el menú está visible
            onDismissRequest = { expanded = false } // Se cierra el menú al hacer clic fuera de él
        ) {
            options.forEach { option -> // Iterar sobre cada opción en la lista
                DropdownMenuItem(
                    text = { Text(option) }, // Muestra el texto de la opción
                    onClick = {
                        onOptionSelected(option) // Llama a la función de devolución de llamada con la opción seleccionada
                        expanded = false // Cierra el menú
                    }
                )
            }
        }
    }
}
