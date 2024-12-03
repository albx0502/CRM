package com.example.crm.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.crm.navigation.AppNavigation
import com.example.crm.theme.CRMTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("MainActivity has started successfully") // Para depuración
        setContent {
            CRMTheme {
                AppNavigation()
            }
        }
    }
}
