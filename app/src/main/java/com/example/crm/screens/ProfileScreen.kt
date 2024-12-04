package com.example.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.crm.R
import com.example.crm.activities.BottomNavigationBar

@Composable
fun ProfileScreen(navController: NavController) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController) // Pasa el NavController para manejar la navegación.
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()), // Permite que la pantalla se pueda desplazar
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Barra de navegación superior
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "Atrás",
                        tint = Color.Black
                    )
                }

                Text(
                    text = "Mi Perfil",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                IconButton(onClick = { /* Acción para configuración */ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_settings),
                        contentDescription = "Configuración",
                        tint = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Imagen de perfil
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier.size(120.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_profile_avatar), // Reemplaza con el recurso de avatar
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(120.dp)
                        .aspectRatio(1f)
                        .clip(CircleShape)
                )
                IconButton(onClick = { /* Acción para editar perfil */ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_edit),
                        contentDescription = "Editar Perfil",
                        tint = Color.Black,
                        modifier = Modifier
                            .size(24.dp)
                            .background(Color.White, CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nombre y usuario (puedes conectar estos datos con la clase User)
            Text(
                text = "Nombre del Usuario", // Sustituir con datos reales del usuario
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Text(
                text = "usuario@email.com", // Sustituir con datos reales del usuario
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Sección de equipo médico
            Text(
                text = "Equipo médico",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                // Aquí puedes añadir las imágenes del equipo médico
                Image(
                    painter = painterResource(id = R.drawable.ic_team_member),
                    contentDescription = "Equipo Médico",
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Image(
                    painter = painterResource(id = R.drawable.ic_team_member),
                    contentDescription = "Equipo Médico",
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "20+",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Opciones del perfil
            val options = listOf(
                Pair("Citas recientes", R.drawable.ic_recent_appointments to { navController.navigate("appointments") }),
                Pair("Chequeos Médicos", R.drawable.ic_medical_checkups to { navController.navigate("medical_checkups") }),
                Pair("Medicamentos Recetados", R.drawable.ic_medicine to { navController.navigate("medications") }),
                Pair("Especialidades Favoritas", R.drawable.ic_favorites to { navController.navigate("favorites") }),
                Pair("Contactos Médicos", R.drawable.ic_contacts to { navController.navigate("medical_contacts") })
            )

            options.forEach { (title, pair) ->
                val (icon, action) = pair
                ProfileOptionItem(title = title, icon = painterResource(id = icon), onClick = action)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun ProfileOptionItem(
    title: String,
    icon: Painter,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F8FA))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = icon,
                    contentDescription = title,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF001F54),
                    fontSize = 16.sp
                )
            }
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Ir a $title",
                tint = Color.Gray
            )
        }
    }
}
