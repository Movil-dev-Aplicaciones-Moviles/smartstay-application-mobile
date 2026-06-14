package com.smartstay.application_mobile_frontend.core.validation

import com.smartstay.application_mobile_frontend.feature.iam.domain.model.RoleHierarchy

/**
 * Validador centralizado de campos de formulario en el frontend.
 *
 * Proporciona funciones de validación local para username, contraseña,
 * confirmación de contraseña, rol e IDs opcionales, evitando enviar
 * datos inválidos al backend y mejorando la experiencia del usuario
 * con mensajes de error inmediatos.
 *
 * Cada función retorna `null` si el valor es válido, o un [String]
 * con el mensaje de error descriptivo si no lo es.
 */
object InputValidator {

    private val usernameRegex = Regex("^[a-zA-Z0-9._@]+$")

    /**
     * Valida un nombre de usuario según las reglas del backend
     * (Username Value Object: 3-100 caracteres, alfanumérico + [. _ @]).
     *
     * Reglas:
     * - No puede estar vacío o en blanco.
     * - Debe tener entre 3 y 100 caracteres.
     * - Solo puede contener letras, números, puntos, guiones bajos y arrobas.
     *
     * @param username El nombre de usuario a validar.
     * @return `null` si es válido, o un mensaje de error descriptivo.
     */
    fun validateUsername(username: String): String? {
        if (username.isBlank()) {
            return "El usuario no puede estar vacío."
        }
        if (username.length < 3 || username.length > 100) {
            return "El usuario debe tener entre 3 y 100 caracteres."
        }
        if (!username.matches(usernameRegex)) {
            return "El usuario solo puede contener letras, números, puntos, guiones bajos y arrobas."
        }
        return null
    }

    /**
     * Valida una contraseña según reglas de complejidad recomendadas.
     *
     * Reglas:
     * - No puede estar vacía.
     * - Mínimo 8 caracteres.
     * - Al menos una letra mayúscula.
     * - Al menos una letra minúscula.
     * - Al menos un dígito.
     *
     * @param password La contraseña a validar.
     * @return `null` si es válida, o un mensaje de error descriptivo.
     */
    fun validatePassword(password: String): String? {
        if (password.isBlank()) {
            return "La contraseña no puede estar vacía."
        }
        if (password.length < 8) {
            return "La contraseña debe tener al menos 8 caracteres."
        }
        if (!password.any { it.isUpperCase() }) {
            return "La contraseña debe contener al menos una letra mayúscula."
        }
        if (!password.any { it.isLowerCase() }) {
            return "La contraseña debe contener al menos una letra minúscula."
        }
        if (!password.any { it.isDigit() }) {
            return "La contraseña debe contener al menos un número."
        }
        return null
    }

    /**
     * Valida que dos contraseñas coincidan.
     *
     * @param password La contraseña original.
     * @param confirmPassword La confirmación de la contraseña.
     * @return `null` si coinciden, o un mensaje de error descriptivo.
     */
    fun validatePasswordConfirmation(password: String, confirmPassword: String): String? {
        return if (password != confirmPassword) {
            "Las contraseñas no coinciden."
        } else {
            null
        }
    }

    /**
     * Valida que un rol sea uno de los valores definidos en [RoleHierarchy].
     *
     * @param role El nombre del rol a validar.
     * @return `null` si es válido, o un mensaje de error descriptivo.
     */
    fun validateRole(role: String): String? {
        if (role.isBlank()) {
            return "El rol no puede estar vacío."
        }
        if (role !in RoleHierarchy.hierarchy.keys) {
            return "Rol no válido. Use: ChainAdmin, HotelAdmin, Recepcionista, Guest, Host."
        }
        return null
    }

    /**
     * Valida un campo opcional que debe ser un número entero no negativo.
     *
     * Reglas:
     * - Si está vacío o en blanco, se considera válido (es opcional).
     * - Si no está vacío, debe ser un número entero válido.
     * - No puede ser negativo.
     *
     * @param value El valor en texto a validar.
     * @param fieldName El nombre del campo para el mensaje de error.
     * @return `null` si es válido o está vacío, o un mensaje de error descriptivo.
     */
    fun validateOptionalId(value: String, fieldName: String): String? {
        if (value.isBlank()) {
            return null
        }
        val number = value.toIntOrNull()
        if (number == null) {
            return "$fieldName debe ser un número entero."
        }
        if (number < 0) {
            return "$fieldName no puede ser negativo."
        }
        return null
    }
}
