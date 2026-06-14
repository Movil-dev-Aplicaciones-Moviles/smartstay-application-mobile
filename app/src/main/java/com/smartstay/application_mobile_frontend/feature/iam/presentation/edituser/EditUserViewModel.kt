package com.smartstay.application_mobile_frontend.feature.iam.presentation.edituser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartstay.application_mobile_frontend.feature.iam.data.dto.UpdateUserRequest
import com.smartstay.application_mobile_frontend.feature.iam.domain.model.User
import com.smartstay.application_mobile_frontend.feature.iam.domain.repository.IamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import com.smartstay.application_mobile_frontend.core.datastore.TokenManager
import com.smartstay.application_mobile_frontend.feature.iam.domain.model.UserPermissions
import kotlinx.coroutines.runBlocking

/**
 * Estado sellado de la pantalla de edición de usuario.
 */
sealed class EditUserUiState {
    data object Idle : EditUserUiState()
    data object Loading : EditUserUiState()
    data class Loaded(val user: User) : EditUserUiState()
    data object Saving : EditUserUiState()
    data object Success : EditUserUiState()
    data class Error(val message: String) : EditUserUiState()
}

/**
 * ViewModel para la pantalla de edición de usuario.
 *
 * Carga un usuario por su ID, gestiona el formulario de edición
 * y envía la actualización parcial al repositorio.
 *
 * @param tokenManager Gestor de tokens para obtener el rol del actor autenticado.
 * @param iamRepository Repositorio IAM para obtener y actualizar usuarios.
 */
@HiltViewModel
class EditUserViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val iamRepository: IamRepository
) : ViewModel() {

    val userPermissions: UserPermissions

    private val _uiState = MutableStateFlow<EditUserUiState>(EditUserUiState.Idle)
    val uiState: StateFlow<EditUserUiState> = _uiState.asStateFlow()

    init {
        val role = runBlocking { tokenManager.getRole() } ?: ""
        userPermissions = UserPermissions(actorRole = role)
    }

    /**
     * Carga un usuario por su ID desde el repositorio.
     *
     * @param userId ID del usuario a cargar.
     */
    fun loadUser(userId: Int) {
        _uiState.value = EditUserUiState.Loading

        viewModelScope.launch {
            try {
                val user = iamRepository.getUserById(userId)
                _uiState.value = EditUserUiState.Loaded(user)
            } catch (e: HttpException) {
                _uiState.value = EditUserUiState.Error(
                    message = e.message ?: "Error al cargar el usuario"
                )
            } catch (e: IOException) {
                _uiState.value = EditUserUiState.Error(
                    message = e.message ?: "Error de conexión"
                )
            } catch (e: Exception) {
                _uiState.value = EditUserUiState.Error(
                    message = e.message ?: "Error desconocido"
                )
            }
        }
    }

    /**
     * Actualiza un usuario con los campos proporcionados.
     *
     * @param userId ID del usuario a actualizar.
     * @param request Datos a actualizar (solo campos no nulos se envían al backend).
     */
    fun updateUser(userId: Int, request: UpdateUserRequest) {
        _uiState.value = EditUserUiState.Saving

        viewModelScope.launch {
            try {
                iamRepository.updateUser(userId, request)
                _uiState.value = EditUserUiState.Success
            } catch (e: HttpException) {
                _uiState.value = EditUserUiState.Error(
                    message = e.message ?: "Error al actualizar el usuario"
                )
            } catch (e: IOException) {
                _uiState.value = EditUserUiState.Error(
                    message = e.message ?: "Error de conexión"
                )
            } catch (e: Exception) {
                _uiState.value = EditUserUiState.Error(
                    message = e.message ?: "Error desconocido"
                )
            }
        }
    }
}
