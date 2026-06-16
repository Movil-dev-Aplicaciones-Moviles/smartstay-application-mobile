package com.smartstay.application_mobile_frontend.feature.iam.presentation.userdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartstay.application_mobile_frontend.feature.iam.data.dto.AssignRoleRequest
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
 * Estado sellado de la pantalla de detalle de usuario.
 */
sealed class UserDetailUiState {
    data object Idle : UserDetailUiState()
    data object Loading : UserDetailUiState()
    data class Success(val user: User) : UserDetailUiState()
    data class Error(val message: String) : UserDetailUiState()
}

/**
 * ViewModel para la pantalla de detalle de usuario.
 *
 * Carga un usuario específico por su ID y expone el estado observable
 * para la capa de presentación.
 *
 * @param tokenManager Gestor de tokens para obtener el rol del actor autenticado.
 * @param iamRepository Repositorio IAM para obtener los datos del usuario.
 */
@HiltViewModel
class UserDetailViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val iamRepository: IamRepository
) : ViewModel() {

    val userPermissions: UserPermissions

    private val _uiState = MutableStateFlow<UserDetailUiState>(UserDetailUiState.Idle)
    val uiState: StateFlow<UserDetailUiState> = _uiState.asStateFlow()

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
        _uiState.value = UserDetailUiState.Loading

        viewModelScope.launch {
            try {
                val user = iamRepository.getUserById(userId)
                _uiState.value = UserDetailUiState.Success(user)
            } catch (e: HttpException) {
                _uiState.value = UserDetailUiState.Error(
                    message = e.message ?: "Error al cargar el usuario"
                )
            } catch (e: IOException) {
                _uiState.value = UserDetailUiState.Error(
                    message = e.message ?: "Error de conexión"
                )
            } catch (e: Exception) {
                _uiState.value = UserDetailUiState.Error(
                    message = e.message ?: "Error desconocido"
                )
            }
        }
    }

    /**
     * Desactiva un usuario (borrado lógico) y recarga sus datos
     * para reflejar el nuevo estado.
     *
     * @param userId ID del usuario a desactivar.
     */
    fun deactivateUser(userId: Int) {
        _uiState.value = UserDetailUiState.Loading

        viewModelScope.launch {
            try {
                iamRepository.deactivateUser(userId)
                loadUser(userId)
            } catch (e: HttpException) {
                _uiState.value = UserDetailUiState.Error(
                    message = e.message ?: "Error al desactivar el usuario"
                )
            } catch (e: IOException) {
                _uiState.value = UserDetailUiState.Error(
                    message = e.message ?: "Error de conexión"
                )
            } catch (e: Exception) {
                _uiState.value = UserDetailUiState.Error(
                    message = e.message ?: "Error desconocido"
                )
            }
        }
    }

    /**
     * Asigna un nuevo rol a un usuario y recarga sus datos
     * para reflejar el cambio.
     *
     * @param userId ID del usuario al que se le asignará el rol.
     * @param role Nombre del nuevo rol a asignar.
     */
    fun assignRole(userId: Int, role: String) {
        _uiState.value = UserDetailUiState.Loading

        viewModelScope.launch {
            try {
                val request = AssignRoleRequest(newRole = role)
                iamRepository.assignRole(userId, request)
                loadUser(userId)
            } catch (e: HttpException) {
                _uiState.value = UserDetailUiState.Error(
                    message = e.message ?: "Error al asignar el rol"
                )
            } catch (e: IOException) {
                _uiState.value = UserDetailUiState.Error(
                    message = e.message ?: "Error de conexión"
                )
            } catch (e: Exception) {
                _uiState.value = UserDetailUiState.Error(
                    message = e.message ?: "Error desconocido"
                )
            }
        }
    }



    fun activateUser(userId: Int) {
        _uiState.value = UserDetailUiState.Loading
        viewModelScope.launch {
            try {
                iamRepository.activateUser(userId)
                loadUser(userId)
            } catch (e: Exception) {
                _uiState.value = UserDetailUiState.Error(e.message ?: "Error al activar el usuario")
            }
        }
    }
}
