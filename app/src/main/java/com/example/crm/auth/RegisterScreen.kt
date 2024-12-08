package com.example.crm.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.crm.R
import com.example.crm.utils.registerWithFirebase
import kotlinx.coroutines.launch

/**
 * **RegisterScreen**
 *
 * Pantalla de registro para nuevos usuarios. Incluye campos de entrada, validaciones y enlaces para redirigir a otras pantallas.
 */
@Composable
fun RegisterScreen(
    onSignUpSuccess: () -> Unit, // Callback cuando el registro es exitoso
    onBackToLoginClick: () -> Unit // Callback para volver a la pantalla de inicio de sesión
) {
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Habilitar scroll para pantallas largas
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título de la pantalla
        Text(
            text = "Regístrate gratis",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Imagen de bienvenida
        Image(
            painter = painterResource(id = R.drawable.welcome_image),
            contentDescription = "Imagen de Registro",
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campos de entrada
        BasicTextFieldWithLabel(value = name, onValueChange = { name = it }, label = "Introduce tu nombre...")
        Spacer(modifier = Modifier.height(12.dp))

        BasicTextFieldWithLabel(value = surname, onValueChange = { surname = it }, label = "Introduce tu apellido...")
        Spacer(modifier = Modifier.height(12.dp))

        BasicTextFieldWithLabel(value = email, onValueChange = { email = it }, label = "Introduce tu correo...")
        Spacer(modifier = Modifier.height(12.dp))

        BasicTextFieldWithLabel(value = phone, onValueChange = { phone = it }, label = "Introduce tu teléfono...")
        Spacer(modifier = Modifier.height(12.dp))

        BasicTextFieldWithLabel(value = gender, onValueChange = { gender = it }, label = "Introduce tu género...")
        Spacer(modifier = Modifier.height(12.dp))

        BasicTextFieldWithLabel(
            value = password,
            onValueChange = { password = it },
            label = "Introduce tu contraseña...",
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(12.dp))

        BasicTextFieldWithLabel(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = "Confirma tu contraseña...",
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botón de Crear cuenta
        Button(
            onClick = {
                if (password == confirmPassword) {
                    registerWithFirebase(
                        email = email,
                        password = password,
                        nombre = name,
                        apellidos = surname,
                        telefono = phone,
                        sexo = gender,
                        onSuccess = {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Registro exitoso")
                            }
                            onSignUpSuccess()
                        },
                        onError = { errorMessage = it }
                    )
                } else {
                    errorMessage = "Las contraseñas no coinciden."
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = "Crear cuenta",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Enlace para volver a la pantalla de inicio de sesión
        Text(
            text = "¿Ya tienes cuenta? Inicia sesión.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(top = 8.dp)
                .clickable { onBackToLoginClick() }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Mostrar error si ocurre
        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

/**
 * **BasicTextFieldWithLabel**
 *
 * Campo de texto básico con etiqueta y estilos personalizados.
 *
 * @param value Texto actual del campo.
 * @param onValueChange Callback para manejar cambios en el texto.
 * @param label Texto de la etiqueta (placeholder).
 * @param visualTransformation Transformación del texto (opcional).
 */
@Composable
fun BasicTextFieldWithLabel(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(
            Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            if (value.isEmpty()) {
                Text(
                    text = label,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = visualTransformation
            )
        }
    }
}
