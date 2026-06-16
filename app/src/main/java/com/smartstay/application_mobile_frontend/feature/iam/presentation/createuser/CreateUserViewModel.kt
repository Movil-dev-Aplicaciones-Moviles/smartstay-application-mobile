package com.smartstay.application_mobile_frontend.feature.iam.presentation.createuser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartstay.application_mobile_frontend.core.datastore.TokenManager
import com.smartstay.application_mobile_frontend.feature.iam.presentation.createuser.CreateUserUiState
import com.smartstay.application_mobile_frontend.feature.iam.data.dto.CreateUserRequest
import com.smartstay.application_mobile_frontend.feature.iam.domain.repository.IamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class CreateUserViewModel @Inject constructor(
    private val iamRepository: IamRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<CreateUserUiState>(CreateUserUiState.Idle)
    val uiState: StateFlow<CreateUserUiState> = _uiState.asStateFlow()

    fun createUser(request: CreateUserRequest) {
        _uiState.value = CreateUserUiState.Loading

        viewModelScope.launch {
            try {
                iamRepository.createUser(request)
                _uiState.value = CreateUserUiState.Success
            } catch (e: HttpException) {
                _uiState.value = CreateUserUiState.Error(
                    message = "Error del servidor: código ${e.code()}. Nombre de usuario duplicado o sin permisos."
                )
            } catch (e: IOException) {
                _uiState.value = CreateUserUiState.Error(
                    message = "Error de conexión. Verifica tu internet."
                )
            } catch (e: Exception) {
                _uiState.value = CreateUserUiState.Error(
                    message = e.message ?: "Error inesperado"
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = CreateUserUiState.Idle
    }
}