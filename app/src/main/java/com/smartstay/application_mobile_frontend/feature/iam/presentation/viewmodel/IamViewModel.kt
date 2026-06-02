// feature/iam/presentation/viewmodel/IamViewModel.kt
package com.smartstay.application_mobile_frontend.feature.iam.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartstay.application_mobile_frontend.feature.iam.domain.model.SignInCommand
import com.smartstay.application_mobile_frontend.feature.iam.domain.model.SignUpCommand
import com.smartstay.application_mobile_frontend.feature.iam.domain.repository.IamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class IamUiState(
    val isSignedIn: Boolean = false,
    val isSignUpSuccess: Boolean = false,
    val isLoading: Boolean = false,
    val role: String = "guest",
    val errors: List<String> = emptyList()
)

@HiltViewModel
class IamViewModel @Inject constructor(
    private val repository: IamRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        IamUiState(
            isSignedIn = repository.isSignedIn(),
            role = if (repository.isSignedIn()) repository.getUserRole() else "guest"
        )
    )
    val uiState = _uiState.asStateFlow()

    fun signIn(command: SignInCommand, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errors = emptyList())
            try {
                val user = repository.signIn(command)
                _uiState.value = _uiState.value.copy(
                    isSignedIn = true,
                    isLoading = false,
                    role = user.role
                )
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSignedIn = false,
                    isLoading = false,
                    role = "guest",
                    errors = listOf(e.message ?: "Error al iniciar sesión")
                )
            }
        }
    }

    fun signUp(command: SignUpCommand, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errors = emptyList())
            try {
                val success = repository.signUp(command)
                if (success) {
                    _uiState.value = _uiState.value.copy(isSignUpSuccess = true, isLoading = false)
                    onSuccess()
                } else {
                    _uiState.value = _uiState.value.copy(
                        isSignUpSuccess = false,
                        isLoading = false,
                        errors = listOf("No se pudo completar el registro")
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSignUpSuccess = false,
                    isLoading = false,
                    errors = listOf(e.message ?: "Error al registrar usuario")
                )
            }
        }
    }

    fun signOut(onSuccess: () -> Unit) {
        repository.signOut()
        _uiState.value = IamUiState(isSignedIn = false, role = "guest")
        onSuccess()
    }
}