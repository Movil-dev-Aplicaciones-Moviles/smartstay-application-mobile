package com.smartstay.application_mobile_frontend.feature.iam.presentation.userlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
 * Estado sellado de la pantalla de lista de usuarios.
 */
sealed class UserListUiState {
    data object Idle : UserListUiState()
    data object Loading : UserListUiState()
    data class Success(val users: List<User>) : UserListUiState()
    data class Error(val message: String) : UserListUiState()
}

/**
 * ViewModel para la pantalla de lista de usuarios.
 *
 * Gestiona la carga de todos los usuarios del alcance del actor autenticado
 * y expone el estado observable para la capa de presentación.
 *
 * @param tokenManager Gestor de tokens para obtener el rol del actor autenticado.
 * @param iamRepository Repositorio IAM para obtener los usuarios.
 */
@HiltViewModel
class UserListViewModel @Inject constructor(
    val tokenManager: TokenManager,
    private val iamRepository: IamRepository
) : ViewModel() {

    val userPermissions: UserPermissions
    val currentUserId: Int = runBlocking { tokenManager.getUserId() } ?: -1

    private val _uiState = MutableStateFlow<UserListUiState>(UserListUiState.Idle)
    val uiState: StateFlow<UserListUiState> = _uiState.asStateFlow()

    init {
        val role = runBlocking { tokenManager.getRole() } ?: ""
        userPermissions = UserPermissions(actorRole = role)
        loadUsers()
    }

    /**
     * Cierra la sesión del administrador actual limpiando de forma
     * asíncrona todos los datos persistidos en el DataStore.
     */
    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                tokenManager.clearSession()
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = UserListUiState.Error(
                    message = e.message ?: "Error al cerrar la sesión"
                )
            }
        }
    }

    /**
     * Carga la lista de usuarios desde el repositorio.
     *
     * Esta función puede ser llamada externamente para realizar un refresco manual.
     */
    fun loadUsers() {
        _uiState.value = UserListUiState.Loading

        viewModelScope.launch {
            try {
                val users = iamRepository.getUsers()
                _uiState.value = UserListUiState.Success(users)
            } catch (e: HttpException) {
                _uiState.value = UserListUiState.Error(
                    message = e.message ?: "Error al obtener usuarios"
                )
            } catch (e: IOException) {
                _uiState.value = UserListUiState.Error(
                    message = e.message ?: "Error de conexión"
                )
            } catch (e: Exception) {
                _uiState.value = UserListUiState.Error(
                    message = e.message ?: "Error desconocido"
                )
            }
        }
    }
}
