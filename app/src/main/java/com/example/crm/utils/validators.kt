package com.example.crm.utils

/**
 * **Clase de validadores genéricos**
 *
 * Esta clase está pensada para contener métodos de validación reutilizables que se
 * pueden usar en diferentes partes de la aplicación.
 *
 * **Ejemplos de validaciones que podríamos incluir:**
 * - Validación de correos electrónicos.
 * - Validación de contraseñas seguras.
 * - Validación de números de teléfono.
 * - Validación de campos vacíos.
 * - Validación de DNI, NIF, etc.
 */
object Validators {

    /**
     * **validarCorreo**
     *
     * Valida si el correo electrónico tiene un formato correcto.
     *
     * **Parámetros:**
     * - `email`: Correo electrónico a validar.
     *
     * **Retorno:**
     * - `true`: Si el correo es válido.
     * - `false`: Si el correo no es válido.
     */
    fun validarCorreo(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        return email.isNotBlank() && email.matches(emailRegex)
    }

    /**
     * **validarContrasena**
     *
     * Valida si la contraseña cumple con los requisitos de seguridad.
     *
     * **Requisitos de la contraseña:**
     * - Al menos 8 caracteres.
     * - Al menos una letra mayúscula.
     * - Al menos una letra minúscula.
     * - Al menos un número.
     * - Al menos un carácter especial (opcional, se puede eliminar si se prefiere).
     *
     * **Parámetros:**
     * - `password`: Contraseña a validar.
     *
     * **Retorno:**
     * - `true`: Si la contraseña es válida.
     * - `false`: Si la contraseña no es válida.
     */
    fun validarContrasena(password: String): Boolean {
        val passwordRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$".toRegex()
        return password.isNotBlank() && password.matches(passwordRegex)
    }

    /**
     * **validarTelefono**
     *
     * Valida si el número de teléfono tiene un formato válido.
     *
     * **Parámetros:**
     * - `telefono`: Número de teléfono a validar.
     *
     * **Retorno:**
     * - `true`: Si el teléfono es válido.
     * - `false`: Si el teléfono no es válido.
     *
     * **Nota:** Este ejemplo solo acepta números con 9 dígitos. Puedes personalizar la
     * expresión regular para admitir otros formatos internacionales.
     */
    fun validarTelefono(telefono: String): Boolean {
        val phoneRegex = "^\\d{9}$".toRegex()
        return telefono.isNotBlank() && telefono.matches(phoneRegex)
    }

    /**
     * **validarDNI**
     *
     * Valida si un DNI español tiene el formato correcto.
     *
     * **Parámetros:**
     * - `dni`: DNI a validar.
     *
     * **Retorno:**
     * - `true`: Si el DNI es válido.
     * - `false`: Si el DNI no es válido.
     *
     * **Lógica:** El DNI tiene 8 dígitos y una letra de control (por ejemplo: 12345678Z).
     * Se valida el formato y la letra de control.
     */
    fun validarDNI(dni: String): Boolean {
        if (dni.length != 9) return false
        val dniNumeros = dni.substring(0, 8).toIntOrNull() ?: return false
        val dniLetra = dni.last()
        val letrasDNI = "TRWAGMYFPDXBNJZSQVHLCKE"
        val letraCorrecta = letrasDNI[dniNumeros % 23]
        return dniLetra.equals(letraCorrecta, ignoreCase = true)
    }

    /**
     * **validarCampoVacio**
     *
     * Verifica si un campo de texto está vacío.
     *
     * **Parámetros:**
     * - `campo`: Texto a validar.
     *
     * **Retorno:**
     * - `true`: Si el campo está vacío.
     * - `false`: Si el campo NO está vacío.
     */
    fun validarCampoVacio(campo: String): Boolean {
        return campo.isBlank()
    }

    /**
     * **validarSoloNumeros**
     *
     * Verifica si una cadena solo contiene números.
     *
     * **Parámetros:**
     * - `input`: Cadena de texto a validar.
     *
     * **Retorno:**
     * - `true`: Si solo contiene números.
     * - `false`: Si contiene letras o caracteres especiales.
     */
    fun validarSoloNumeros(input: String): Boolean {
        val numerosRegex = "^\\d+$".toRegex()
        return input.isNotBlank() && input.matches(numerosRegex)
    }

    /**
     * **validarLongitudMinima**
     *
     * Verifica si una cadena tiene una longitud mínima.
     *
     * **Parámetros:**
     * - `input`: Cadena de texto a validar.
     * - `minLength`: Longitud mínima requerida.
     *
     * **Retorno:**
     * - `true`: Si cumple con la longitud mínima.
     * - `false`: Si no cumple con la longitud mínima.
     */
    fun validarLongitudMinima(input: String, minLength: Int): Boolean {
        return input.length >= minLength
    }
}
