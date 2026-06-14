package com.smartstay.application_mobile_frontend.feature.iam.domain.model

/**
 * Modelo de dominio que agrupa el token JWT y los datos del usuario autenticado.
 *
 * Se obtiene como resultado de un inicio de sesión (signIn) o registro (signUp) exitoso.
 * Este modelo es la fuente de verdad para la sesión activa en el frontend.
 *
 * @property token Token JWT de autenticación.
 * @property user Datos del usuario autenticado.
 *
 * El mapeo desde AuthenticatedUserResponse se implementará en IamMapper (tarea 1.3).
 */
data class AuthenticatedUser(
    val token: String,
    val user: User
)
