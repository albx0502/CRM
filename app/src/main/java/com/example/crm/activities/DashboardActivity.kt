package com.example.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.crm.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet


class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DashboardScreen(navController = null) // Pasar NavController real cuando sea posible
        }
    }
}

@Composable
fun DashboardScreen(navController: NavController?) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController) // Pasamos el NavController aquí
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título Principal: Gestión de Pacientes
            Text(
                text = "Gestión de pacientes",
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFF001F54)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de Citas Programadas
            Button(
                onClick = { /* Acción del botón */ },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF002F6C)),
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(55.dp)
            ) {
                Text(text = "Citas programadas", color = Color.White, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Visión General de citas - Gráfico
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Visión General de citas programadas",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Creación del gráfico utilizando MPAndroidChart
                    val entries = listOf(
                        Entry(1f, 5f),
                        Entry(2f, 3f),
                        Entry(3f, 4f),
                        Entry(4f, 6f)
                    )
                    val dataSet = LineDataSet(entries, "Consultas").apply {
                        color = android.graphics.Color.MAGENTA
                        setCircleColor(android.graphics.Color.MAGENTA)
                    }
                    val lineData = LineData(dataSet)

                    AndroidView(
                        factory = { context ->
                            LineChart(context).apply {
                                data = lineData
                                description.text = "Gráfico de Citas" // Añadir descripción
                                setDrawGridBackground(false)
                                setTouchEnabled(true)
                                setPinchZoom(true)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )

                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tarjeta de Total de Citas Programadas
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF003366)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Total de citas programadas",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White
                        )
                        Text(
                            text = "30",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White
                        )
                    }
                    Button(onClick = { /* Acción botón Mes */ }) {
                        Text(text = "Mes", color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Servicios Populares
            Text(
                text = "Servicios Populares",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF001F54)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tarjeta de Servicios Populares - Citas Recientes
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable {
                        navController?.navigate("profile") // Navegar a la pantalla de perfil al hacer clic
                    },
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_recent_appointments),
                        contentDescription = "Citas Recientes",
                        modifier = Modifier.size(48.dp)
                    )
                    Column {
                        Text(text = "Citas recientes", fontWeight = FontWeight.Bold, color = Color(0xFF001F54))
                        Text(text = "30 Consulta mes", color = Color.Green)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))


                }
            }
        }


@Composable
fun BottomNavigationBar(navController: NavController?) {
    NavigationBar(
        containerColor = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        tonalElevation = 8.dp
    ) {
        // Home Navigation Item
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_home),
                    contentDescription = "Home"
                )
            },
            label = { Text("Home") },
            selected = false, // Controla si está seleccionado según la pantalla actual
            onClick = {
                navController?.navigate("dashboard") {
                    popUpTo("dashboard") { inclusive = true }
                }
            }
        )

        // Calendario Navigation Item
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_calendar),
                    contentDescription = "Calendario"
                )
            },
            label = { Text("Calendario") },
            selected = false,
            onClick = {
                // Implementa la navegación a la pantalla de calendario
            }
        )

        // Perfil Navigation Item
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_profile),
                    contentDescription = "Perfil"
                )
            },
            label = { Text("Perfil") },
            selected = false,
            onClick = {
                navController?.navigate("profile") {
                    popUpTo("profile") { inclusive = true }
                }
            }
        )
    }
}
