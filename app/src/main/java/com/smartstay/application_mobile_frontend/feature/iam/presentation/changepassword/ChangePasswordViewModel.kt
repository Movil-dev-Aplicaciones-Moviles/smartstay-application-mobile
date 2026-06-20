package com.smartstay.application_mobile_frontend.feature.iam.presentation.changepassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartstay.application_mobile_frontend.feature.iam.data.dto.ChangePasswordRequest
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
 * Estado sellado de la pantalla de cambio de contraseña.
 */
sealed class ChangePasswordUiState {
    data object Idle : ChangePasswordUiState()
    data object Loading : ChangePasswordUiState()
    data object Success : ChangePasswordUiState()
    data class Error(val message: String) : ChangePasswordUiState()
}

/**
 * ViewModel para la pantalla de cambio de contraseña.
 *
 * Gestiona el estado del cambio de contraseña y orquesta la llamada
 * al repositorio IAM para actualizar la contraseña del usuario autenticado.
 *
 * @param iamRepository Repositorio de autenticación IAM.
 */
@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val iamRepository: IamRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ChangePasswordUiState>(ChangePasswordUiState.Idle)
    val uiState: StateFlow<ChangePasswordUiState> = _uiState.asStateFlow()

    /**
     * Cambia la contraseña del usuario autenticado.
     *
     * @param currentPassword Contraseña actual del usuario.
     * @param newPassword Nueva contraseña del usuario.
     */
    fun changePassword(currentPassword: String, newPassword: String) {
        _uiState.value = ChangePasswordUiState.Loading

        viewModelScope.launch {
            try {
                val request = ChangePasswordRequest(
                    currentPassword = currentPassword,
                    newPassword = newPassword
                )
                iamRepository.changePassword(request)
                _uiState.value = ChangePasswordUiState.Success
            } catch (e: HttpException) {
                _uiState.value = ChangePasswordUiState.Error(
                    message = e.message ?: "Error del servidor"
                )
            } catch (e: IOException) {
                _uiState.value = ChangePasswordUiState.Error(
                    message = "Error de conexión. Verifica tu internet."
                )
            } catch (e: Exception) {
                _uiState.value = ChangePasswordUiState.Error(
                    message = e.message ?: "Error desconocido"
                )
            }
        }
    }

    /**
     * Restablece el estado a [Idle] después de mostrar un mensaje de éxito.
     */
    fun resetState() {
        _uiState.value = ChangePasswordUiState.Idle
    }
}
