package com.example.crm.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.crm.navigation.AppNavigation
import com.example.crm.theme.CRMTheme
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Punto de entrada principal de la aplicación.
 * Configura Firebase y establece la navegación y el tema general.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Habilitar el registro de logs de Firebase Firestore (útil para depuración)
        FirebaseFirestore.setLoggingEnabled(true)

        // Establecer el contenido de la app con Jetpack Compose
        setContent {
            CRMTheme {
                AppNavigation()
            }
        }
    }
}
