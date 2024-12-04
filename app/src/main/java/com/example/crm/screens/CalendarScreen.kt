package com.example.screens

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
//import com.example.crm.activities.BottomNavigationBar
import java.util.*

@Composable
fun CalendarScreen(navController: NavController?) {
    // Estado para almacenar la fecha seleccionada
    var selectedDate by remember { mutableStateOf("") }

    // Configuración del DatePicker para seleccionar una fecha
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        LocalContext.current,
        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
            selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
        }, year, month, day
    )

    Scaffold(
//        bottomBar = {
//            BottomNavigationBar(navController) // Agrega la barra de navegación inferior con la pantalla seleccionada
//        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título principal
            Text(
                text = "Programa recordatorios de salud",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtítulo
            Text(
                text = "Selecciona la fecha y hora para recibir un recordatorio de tus chequeos.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón para mostrar el DatePickerDialog
            Button(
                onClick = {
                    datePickerDialog.show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF002F6C))
            ) {
                Text(text = if (selectedDate.isEmpty()) "Seleccionar fecha" else "Fecha: $selectedDate", color = Color.White)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón para agregar recordatorio
            Button(
                onClick = {
                    // Acción para agregar el recordatorio con la fecha seleccionada
                    if (selectedDate.isNotEmpty()) {
                        // Aquí podrían ustedes agregar la lógica para enviar la información al backend
                        navController?.popBackStack() // Navegar de regreso después de agregar recordatorio
                    }
                },
                enabled = selectedDate.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF002F6C))
            ) {
                Text(text = "Agregar recordatorio", color = Color.White, fontSize = 16.sp)
            }
        }
    }
}


