package com.smartstay.application_mobile_frontend.feature.iam.domain.repository

import com.smartstay.application_mobile_frontend.feature.iam.data.dto.AssignRoleRequest
import com.smartstay.application_mobile_frontend.feature.iam.data.dto.ChangePasswordRequest
import com.smartstay.application_mobile_frontend.feature.iam.data.dto.CreateUserRequest
import com.smartstay.application_mobile_frontend.feature.iam.data.dto.SignInRequest
import com.smartstay.application_mobile_frontend.feature.iam.data.dto.SignUpRequest
import com.smartstay.application_mobile_frontend.feature.iam.data.dto.UpdateUserRequest
import com.smartstay.application_mobile_frontend.feature.iam.domain.model.AuthenticatedUser
import com.smartstay.application_mobile_frontend.feature.iam.domain.model.User

/**
 * Contrato del repositorio de autenticación y gestión de usuarios (IAM).
 *
 * Define las operaciones de autenticación disponibles en el dominio,
 * desacopladas de la implementación concreta de red y almacenamiento.
 */
interface IamRepository {

    /**
     * Inicia sesión con las credenciales proporcionadas.
     *
     * @param request Credenciales de inicio de sesión.
     * @return Usuario autenticado con token JWT.
     */
    suspend fun signIn(request: SignInRequest): AuthenticatedUser

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param request Datos de registro del nuevo usuario.
     * @return Usuario autenticado con token JWT.
     */
    suspend fun signUp(request: SignUpRequest): AuthenticatedUser

    /**
     * Cambia la contraseña del usuario autenticado.
     *
     * @param request Contraseña actual y nueva contraseña.
     */
    suspend fun changePassword(request: ChangePasswordRequest)

    /**
     * Obtiene la lista de todos los usuarios visibles en el alcance del rol actual.
     *
     * @return Lista de usuarios del sistema.
     */
    suspend fun getUsers(): List<User>

    /**
     * Obtiene un usuario por su ID.
     *
     * @param userId ID del usuario a consultar.
     * @return Usuario solicitado.
     */
    suspend fun getUserById(userId: Int): User

    /**
     * Crea un nuevo usuario (operación administrativa).
     *
     * @param request Datos del nuevo usuario.
     * @return Usuario creado con su ID asignado.
     */
    suspend fun createUser(request: CreateUserRequest): User

    /**
     * Actualiza parcialmente un usuario existente.
     *
     * @param userId ID del usuario a actualizar.
     * @param request Campos a modificar. Solo los no nulos se aplican.
     * @return Usuario con los datos actualizados.
     */
    suspend fun updateUser(userId: Int, request: UpdateUserRequest): User

    /**
     * Desactiva un usuario (borrado lógico, no físico).
     *
     * @param userId ID del usuario a desactivar.
     */
    suspend fun deactivateUser(userId: Int)

    /**
     * Asigna un nuevo rol a un usuario existente.
     *
     * @param userId ID del usuario al que asignar el rol.
     * @param request Rol a asignar.
     */
    suspend fun assignRole(userId: Int, request: AssignRoleRequest)
}
