package com.smartstay.application_mobile_frontend.feature.iam.data.dto

/**
 * DTO para la petición de inicio de sesión.
 * Se envía a: POST /api/v1/authentication/sign-in
 */
data class SignInRequest(
    val username: String,
    val password: String
)

/**
 * DTO para la petición de registro de usuario.
 * Se envía a: POST /api/v1/authentication/sign-up
 *
 * @property role Rol opcional del usuario. Si se omite, el backend asigna uno por defecto.
 */
data class SignUpRequest(
    val username: String,
    val password: String,
    val role: String? = null
)

/**
 * DTO que representa la respuesta de autenticación (sign-in / sign-up).
 * Contiene el token JWT y los datos del usuario autenticado.
 */
data class AuthenticatedUserResponse(
    val token: String,
    val id: Int,
    val username: String,
    val role: String,
    val status: String? = null,
    val hotelId: Int? = null,
    val chainId: Int? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

/**
 * DTO que representa un usuario devuelto por el backend.
 * Mapea el UserResource del backend.
 */
data class UserResponse(
    val id: Int,
    val username: String,
    val role: String,
    val status: String?,
    val hotelId: Int? = null,
    val chainId: Int? = null,
    val tokenVersion: Int? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

/**
 * DTO genérico para respuestas que solo contienen un mensaje.
 * Se usa en endpoints como cambio de contraseña, desactivación, etc.
 */
data class MessageResponse(
    val message: String
)

/**
 * DTO para la petición de cambio de contraseña.
 * Se envía a: POST /api/v1/users/change-password
 */
data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)

/**
 * DTO para crear un usuario. Se envía a: POST /api/v1/users
 *
 * @property hotelId ID del hotel al que pertenece. Nulo si el usuario no está asignado a un hotel.
 * @property chainId ID de la cadena hotelera a la que pertenece. Nulo si no aplica.
 */
data class CreateUserRequest(
    val username: String,
    val password: String,
    val role: String,
    val hotelId: Int? = null,
    val chainId: Int? = null
)

/**
 * DTO para actualización parcial de un usuario. Se envía a: PUT /api/v1/users/{id}
 *
 * Todos los campos son opcionales; el backend solo actualiza los campos enviados.
 */
data class UpdateUserRequest(
    val username: String? = null,
    val role: String? = null,
    val hotelId: Int? = null,
    val chainId: Int? = null,
    val status: String? = null
)

/**
 * DTO para asignar un rol a un usuario. Se envía a: POST /api/v1/users/{id}/assign-role
 */
data class AssignRoleRequest(
    val role: String
)
