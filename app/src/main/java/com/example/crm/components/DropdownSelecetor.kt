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

@Composable
fun SimpleDropdownSelector(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) } // Controla si el menú está expandido

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { expanded = true } // Hace que toda el área sea clicable
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp) // Espaciado interno del cuadro
    ) {
        // Texto del selector
        Text(
            text = if (selectedOption.isEmpty()) label else selectedOption,
            style = MaterialTheme.typography.bodyLarge,
            color = if (selectedOption.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
        )

        // Menú desplegable
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false // Cierra el menú al seleccionar una opción
                    }
                )
            }
        }
    }
}
