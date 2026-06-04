// feature/iam/presentation/viewmodel/IamViewModel.kt
package com.smartstay.application_mobile_frontend.feature.iam.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartstay.application_mobile_frontend.feature.iam.domain.model.SignInCommand
import com.smartstay.application_mobile_frontend.feature.iam.domain.model.SignUpCommand
import com.smartstay.application_mobile_frontend.feature.iam.domain.model.UserRole
import com.smartstay.application_mobile_frontend.feature.iam.domain.repository.IamRepository
import com.smartstay.application_mobile_frontend.feature.iam.presentation.navigation.AppRoutes
import com.smartstay.application_mobile_frontend.feature.iam.presentation.navigation.RoleDestinationResolver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * State definitions for the Authentication UI.
 */
data class IamUiState(
    val isSignedIn: Boolean = false,
    val isSignUpSuccess: Boolean = false,
    val isLoading: Boolean = false,
    val role: String = "guest",
    val errors: List<String> = emptyList()
)

/**
 * Presentation logic controller orchestrating Identity and Access Management operations.
 * Employs unidirectional data flow and handles domain model commands.
 */
@Suppress("SpellCheckingInspection") // Evita que marque los textos en español como errores
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

    private val _events = Channel<AuthEvent>()
    val events = _events.receiveAsFlow()

    /**
     * Processes the authentication command and manages routing based on the authorized role.
     * Blocks users with the [UserRole.GUEST] role from entering the application operational flows.
     */
    fun signIn(command: SignInCommand) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errors = emptyList())

            try {
                val user = repository.signIn(command)

                if (user.role == UserRole.GUEST) {
                    val errorMessage = "Acceso denegado: Los invitados no tienen acceso a los paneles administrativos."
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errors = listOf(errorMessage)
                    )
                    _events.send(AuthEvent.Error(errorMessage))
                    repository.signOut()
                    return@launch
                }

                _uiState.value = _uiState.value.copy(
                    isSignedIn = true,
                    isLoading = false,
                    role = user.role.value
                )

                _events.send(
                    AuthEvent.Navigate(
                        RoleDestinationResolver.resolve(user.role)
                    )
                )
            } catch (e: Exception) {
                val message = e.message ?: "Error de autenticación"
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errors = listOf(message)
                )
                _events.send(AuthEvent.Error(message))
            }
        }
    }

    /**
     * Executes the registration command for new users.
     */
    fun signUp(command: SignUpCommand) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errors = emptyList())

            try {
                val success = repository.signUp(command)

                if (success) {
                    _uiState.value = _uiState.value.copy(isSignUpSuccess = true, isLoading = false)
                    _events.send(AuthEvent.Navigate(AppRoutes.SIGN_IN))
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

    /**
     * Terminates the current session and redirects to the sign-in screen.
     */
    fun signOut() {
        repository.signOut()
        _uiState.value = IamUiState(isSignedIn = false, role = "guest")
        viewModelScope.launch {
            _events.send(AuthEvent.Navigate(AppRoutes.SIGN_IN))
        }
    }

    /**
     * Clears existing UI errors to reset state.
     */
    fun clearErrors() {
        _uiState.value = _uiState.value.copy(errors = emptyList())
    }
}