package com.smartstay.application_mobile_frontend.feature.iam.data.repository

import com.smartstay.application_mobile_frontend.core.datastore.TokenManager
import com.smartstay.application_mobile_frontend.feature.iam.data.dto.AssignRoleRequest
import com.smartstay.application_mobile_frontend.feature.iam.data.dto.ChangePasswordRequest
import com.smartstay.application_mobile_frontend.feature.iam.data.dto.CreateUserRequest
import com.smartstay.application_mobile_frontend.feature.iam.data.dto.SignInRequest
import com.smartstay.application_mobile_frontend.feature.iam.data.dto.SignUpRequest
import com.smartstay.application_mobile_frontend.feature.iam.data.dto.UpdateUserRequest
import com.smartstay.application_mobile_frontend.feature.iam.data.mapper.toDomain
import com.smartstay.application_mobile_frontend.feature.iam.data.remote.IamApiService
import com.smartstay.application_mobile_frontend.feature.iam.domain.model.AuthenticatedUser
import com.smartstay.application_mobile_frontend.feature.iam.domain.model.User
import com.smartstay.application_mobile_frontend.feature.iam.domain.repository.IamRepository
import javax.inject.Inject

/**
 * Implementación concreta de [IamRepository].
 *
 * Orquesta las llamadas a la API mediante [IamApiService], convierte los DTOs
 * a modelos de dominio y gestiona la persistencia de sesión a través de [TokenManager].
 *
 * @param api Servicio Retrofit para las llamadas HTTP de IAM.
 * @param tokenManager Gestor de persistencia del token JWT y datos de sesión.
 */
class IamRepositoryImpl @Inject constructor(
    private val api: IamApiService,
    private val tokenManager: TokenManager
) : IamRepository {

    /**
     * Inicia sesión, convierte la respuesta y persiste la sesión.
     */
    override suspend fun signIn(request: SignInRequest): AuthenticatedUser {
        val response = api.signIn(request)
        val authenticatedUser = response.toDomain()

        tokenManager.saveAuthData(
            token = authenticatedUser.token,
            userId = authenticatedUser.user.id,
            username = authenticatedUser.user.username,
            role = authenticatedUser.user.role,
            hotelId = authenticatedUser.user.hotelId,
            chainId = authenticatedUser.user.chainId
        )

        return authenticatedUser
    }

    /**
     * Registra un nuevo usuario, convierte la respuesta y persiste la sesión.
     */
    override suspend fun signUp(request: SignUpRequest): AuthenticatedUser {
        val response = api.signUp(request)
        val authenticatedUser = response.toDomain()

        tokenManager.saveAuthData(
            token = authenticatedUser.token,
            userId = authenticatedUser.user.id,
            username = authenticatedUser.user.username,
            role = authenticatedUser.user.role,
            hotelId = authenticatedUser.user.hotelId,
            chainId = authenticatedUser.user.chainId
        )

        return authenticatedUser
    }

    /**
     * Cambia la contraseña del usuario autenticado.
     * Descarta la respuesta [MessageResponse] y retorna [Unit].
     */
    override suspend fun changePassword(request: ChangePasswordRequest) {
        api.changePassword(request)
    }

    /**
     * Obtiene la lista de todos los usuarios y convierte cada DTO a modelo de dominio.
     */
    override suspend fun getUsers(): List<User> {
        val response = api.getUsers()
        return response.toDomain()
    }

    /**
     * Obtiene un usuario por su ID y lo convierte a modelo de dominio.
     */
    override suspend fun getUserById(userId: Int): User {
        val response = api.getUserById(userId)
        return response.toDomain()
    }

    /**
     * Crea un nuevo usuario y retorna el modelo de dominio resultante.
     */
    override suspend fun createUser(request: CreateUserRequest) {
        api.createUser(request)
    }

    /**
     * Actualiza parcialmente un usuario y retorna el modelo de dominio actualizado.
     */
    override suspend fun updateUser(userId: Int, request: UpdateUserRequest) {
        api.updateUser(userId, request)
    }

    /**
     * Desactiva un usuario. Descarta el [MessageResponse] devuelto por la API.
     */
    override suspend fun deactivateUser(userId: Int) {
        api.deactivateUser(userId)
    }

    /**
     * Asigna un rol a un usuario. Descarta el [MessageResponse] devuelto por la API.
     */
    override suspend fun assignRole(userId: Int, request: AssignRoleRequest) {
        api.assignRole(userId, request)
    }

    override suspend fun activateUser(userId: Int) {
        api.activateUser(userId)
    }
}
