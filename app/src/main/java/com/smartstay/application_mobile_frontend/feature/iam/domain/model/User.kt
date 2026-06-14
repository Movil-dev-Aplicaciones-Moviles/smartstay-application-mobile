package com.smartstay.application_mobile_frontend.feature.iam.domain.model

/**
 * Modelo de dominio que representa un usuario del sistema.
 *
 * Contiene los datos básicos del usuario independientemente de su estado de autenticación.
 * Se obtiene tras un inicio de sesión exitoso o al consultar/listar usuarios.
 *
 * @property id Identificador único del usuario.
 * @property username Nombre de usuario o correo electrónico.
 * @property role Rol del usuario (ej: ChainAdmin, HotelAdmin, Guest, Host).
 * @property status Estado del usuario (ej: Active, Inactive).
 * @property hotelId Hotel asociado al usuario (opcional, para multi‑tenant).
 * @property chainId Cadena hotelera asociada al usuario (opcional, para multi‑tenant).
 * @property createdAt Fecha de creación del usuario en formato ISO 8601.
 * @property updatedAt Fecha de última actualización del usuario en formato ISO 8601.
 *
 * El mapeo desde UserResponse se implementará en IamMapper (tarea 1.3).
 */
data class User(
    val id: Int,
    val username: String,
    val role: String,
    val status: String,
    val hotelId: Int? = null,
    val chainId: Int? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)
