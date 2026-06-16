package com.smartstay.application_mobile_frontend.feature.iam.data.remote

import com.smartstay.application_mobile_frontend.feature.iam.data.dto.AssignRoleRequest
import com.smartstay.application_mobile_frontend.feature.iam.data.dto.AuthenticatedUserResponse
import com.smartstay.application_mobile_frontend.feature.iam.data.dto.ChangePasswordRequest
import com.smartstay.application_mobile_frontend.feature.iam.data.dto.CreateUserRequest
import com.smartstay.application_mobile_frontend.feature.iam.data.dto.MessageResponse
import com.smartstay.application_mobile_frontend.feature.iam.data.dto.SignInRequest
import com.smartstay.application_mobile_frontend.feature.iam.data.dto.SignUpRequest
import com.smartstay.application_mobile_frontend.feature.iam.data.dto.UpdateUserRequest
import com.smartstay.application_mobile_frontend.feature.iam.data.dto.UserResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Interfaz Retrofit para el servicio de autenticación y gestión de usuarios (IAM).
 */
interface IamApiService {

    /**
     * Inicia sesión con credenciales de usuario.
     *
     * @param request Credenciales de inicio de sesión.
     * @return Respuesta de autenticación con token JWT y datos del usuario.
     */
    @POST("authentication/sign-in")
    suspend fun signIn(@Body request: SignInRequest): AuthenticatedUserResponse

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param request Datos de registro del nuevo usuario.
     * @return Respuesta de autenticación con token JWT y datos del usuario creado.
     */
    @POST("authentication/sign-up")
    suspend fun signUp(@Body request: SignUpRequest): AuthenticatedUserResponse

    /**
     * Cambia la contraseña del usuario autenticado.
     *
     * @param request Contraseña actual y nueva contraseña.
     * @return Mensaje de confirmación del cambio.
     */
    @POST("users/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): MessageResponse

    /**
     * Obtiene la lista de todos los usuarios.
     *
     * @return Lista de usuarios registrados en el sistema.
     */
    @GET("users")
    suspend fun getUsers(): List<UserResponse>

    /**
     * Obtiene un usuario por su ID.
     *
     * @param userId ID del usuario a consultar.
     * @return Datos del usuario solicitado.
     */
    @GET("users/{id}")
    suspend fun getUserById(@Path("id") userId: Int): UserResponse

    /**
     * Crea un nuevo usuario (gestión administrativa).
     *
     * @param request Datos del usuario a crear.
     * @return Usuario creado con su ID asignado.
     */
    @POST("users")
    suspend fun createUser(@Body request: CreateUserRequest): MessageResponse

    /**
     * Actualiza parcialmente un usuario existente.
     *
     * @param userId ID del usuario a actualizar.
     * @param request Campos a modificar. Solo se actualizan los campos no nulos.
     * @return Usuario con los datos actualizados.
     */
    @PUT("users/{id}")
    suspend fun updateUser(
        @Path("id") userId: Int,
        @Body request: UpdateUserRequest
    ): MessageResponse

    /**
     * Desactiva un usuario (no lo elimina físicamente).
     *
     * @param userId ID del usuario a desactivar.
     * @return Mensaje de confirmación de la desactivación.
     */
    @DELETE("users/{id}")
    suspend fun deactivateUser(@Path("id") userId: Int): MessageResponse

    /**
     * Asigna un rol a un usuario existente.
     *
     * @param userId ID del usuario al que se le asignará el rol.
     * @param request Rol a asignar.
     * @return Mensaje de confirmación de la asignación.
     */
    @POST("users/{id}/assign-role")
    suspend fun assignRole(
        @Path("id") userId: Int,
        @Body request: AssignRoleRequest
    ): MessageResponse

    /**
     * Reactiva un usuario previamente desactivado.
     *
     * @param userId ID del usuario a reactivar.
     * @return Mensaje de confirmación de la reactivación.
     */
    @POST("users/{id}/activate")
    suspend fun activateUser(@Path("id") userId: Int): MessageResponse
}
