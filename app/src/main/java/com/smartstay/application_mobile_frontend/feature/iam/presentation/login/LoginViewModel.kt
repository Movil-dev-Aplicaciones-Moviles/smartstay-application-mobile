package com.smartstay.application_mobile_frontend.feature.iam.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartstay.application_mobile_frontend.feature.iam.data.dto.SignInRequest
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
 * Estado sellado de la pantalla de inicio de sesión.
 */
sealed class LoginUiState {
    data object Idle : LoginUiState()
    data object Loading : LoginUiState()
    data class Success(val authenticatedUser: AuthenticatedUser) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

/**
 * ViewModel para la pantalla de inicio de sesión.
 *
 * Gestiona el estado de autenticación y orquesta la llamada
 * al repositorio IAM para iniciar sesión.
 *
 * @param iamRepository Repositorio de autenticación IAM.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val iamRepository: IamRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    /**
     * Inicia sesión con las credenciales proporcionadas.
     *
     * @param username Nombre de usuario o correo electrónico.
     * @param password Contraseña del usuario.
     */
    fun signIn(username: String, password: String) {
        _uiState.value = LoginUiState.Loading

        viewModelScope.launch {
            try {
                val request = SignInRequest(username = username, password = password)
                val authenticatedUser = iamRepository.signIn(request)
                _uiState.value = LoginUiState.Success(authenticatedUser)
            } catch (e: HttpException) {
                _uiState.value = LoginUiState.Error(
                    message = e.message ?: "Error de autenticación"
                )
            } catch (e: IOException) {
                _uiState.value = LoginUiState.Error(
                    message = e.message ?: "Error de conexión"
                )
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error(
                    message = e.message ?: "Error desconocido"
                )
            }
        }
    }
}
