package com.example.crm.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.crm.navigation.AppNavigation
import com.example.crm.theme.CRMTheme
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Activar el registro de logs para Firebase Firestore
        FirebaseFirestore.setLoggingEnabled(true)

        setContent {
            CRMTheme {
                AppNavigation()
            }
        }
    }
}
