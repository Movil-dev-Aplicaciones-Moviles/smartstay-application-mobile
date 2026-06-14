package com.smartstay.application_mobile_frontend.feature.iam.presentation.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartstay.application_mobile_frontend.feature.iam.data.dto.SignUpRequest
import com.smartstay.application_mobile_frontend.feature.iam.domain.model.AuthenticatedUser
import com.smartstay.application_mobile_frontend.feature.iam.domain.repository.IamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/**
 * Estado sellado de la pantalla de registro.
 */
sealed class SignUpUiState {
    data object Idle : SignUpUiState()
    data object Loading : SignUpUiState()
    data class Success(val authenticatedUser: AuthenticatedUser) : SignUpUiState()
    data class Error(val message: String) : SignUpUiState()
}

/**
 * ViewModel para la pantalla de registro de usuario.
 *
 * Gestiona el estado del registro y orquesta la llamada
 * al repositorio IAM para crear un nuevo usuario.
 *
 * @param iamRepository Repositorio de autenticación IAM.
 */
@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val iamRepository: IamRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SignUpUiState>(SignUpUiState.Idle)
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param username Nombre de usuario.
     * @param password Contraseña del usuario.
     * @param role Rol opcional del usuario (ej: GUEST, HOST).
     */
    fun signUp(username: String, password: String, role: String? = null) {
        _uiState.value = SignUpUiState.Loading

        viewModelScope.launch {
            try {
                val request = SignUpRequest(
                    username = username,
                    password = password,
                    role = role
                )
                val authenticatedUser = iamRepository.signUp(request)
                _uiState.value = SignUpUiState.Success(authenticatedUser)
            } catch (e: HttpException) {
                _uiState.value = SignUpUiState.Error(
                    message = "Error del servidor: ${e.code()}"
                )
            } catch (e: IOException) {
                _uiState.value = SignUpUiState.Error(
                    message = "Error de conexión. Verifica tu internet."
                )
            } catch (e: Exception) {
                _uiState.value = SignUpUiState.Error(
                    message = e.message ?: "Error desconocido"
                )
            }
        }
    }
}
