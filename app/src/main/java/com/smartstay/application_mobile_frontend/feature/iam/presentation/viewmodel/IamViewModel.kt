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
 * UI state snapshot for all Identity and Access Management screens.
 *
 * Immutable data class — every change produces a new copy via [copy].
 *
 * @property isSignedIn     True when a valid, non-expired local session exists.
 * @property isSignUpSuccess True after a successful registration (resets on sign-in).
 * @property isLoading      True while a network operation is in flight.
 * @property role           String representation of the current [UserRole].
 *                          Defaults to "guest" when no session is active.
 * @property errors         Ordered list of human-readable error messages to display.
 *                          Empty when no errors are present.
 */
data class IamUiState(
    val isSignedIn:     Boolean      = false,
    val isSignUpSuccess: Boolean     = false,
    val isLoading:      Boolean      = false,
    val role:           String       = UserRole.GUEST.value,
    val errors:         List<String> = emptyList()
)

/**
 * Presentation logic controller for Identity and Access Management.
 *
 * Employs unidirectional data flow:
 *   UI action → ViewModel intent → repository call → state/event emission → UI recompose.
 *
 * One-shot side effects (navigation, transient errors) travel through [events] (a Channel)
 * so they are consumed exactly once and are not replayed on recomposition.
 */
@HiltViewModel
class IamViewModel @Inject constructor(
    private val repository: IamRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        IamUiState(
            isSignedIn = repository.isSignedIn(),
            role       = if (repository.isSignedIn()) repository.getUserRole()
            else UserRole.GUEST.value
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _events = Channel<AuthEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    // ── Sign in ───────────────────────────────────────────────────────────────

    /**
     * Authenticates the user with [command] and navigates to the role dashboard.
     *
     * GUEST users are blocked immediately after the repository call — their
     * session is cleared and an error message is surfaced without navigating.
     */
    fun signIn(command: SignInCommand) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errors = emptyList())

            try {
                val user = repository.signIn(command)

                if (user.role == UserRole.GUEST) {
                    val message = "Acceso denegado: los invitados no tienen acceso a los paneles operativos."
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errors    = listOf(message)
                    )
                    _events.send(AuthEvent.Error(message))
                    repository.signOut()
                    return@launch
                }

                _uiState.value = _uiState.value.copy(
                    isSignedIn = true,
                    isLoading  = false,
                    role       = user.role.value
                )

                _events.send(AuthEvent.Navigate(RoleDestinationResolver.resolve(user.role)))

            } catch (e: Exception) {
                val message = e.message ?: "Error de autenticación."
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errors    = listOf(message)
                )
                _events.send(AuthEvent.Error(message))
            }
        }
    }

    // ── Sign up ───────────────────────────────────────────────────────────────

    /**
     * Registers a new user with [command] and redirects to the sign-in screen.
     *
     * No session is created here — the user must sign in after registration.
     */
    fun signUp(command: SignUpCommand) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errors = emptyList())

            try {
                val success = repository.signUp(command)

                if (success) {
                    _uiState.value = _uiState.value.copy(
                        isSignUpSuccess = true,
                        isLoading       = false
                    )
                    _events.send(AuthEvent.Navigate(AppRoutes.SIGN_IN))
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errors    = listOf("No se pudo completar el registro.")
                    )
                }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errors    = listOf(e.message ?: "Error al registrar usuario.")
                )
            }
        }
    }

    // ── Sign out ──────────────────────────────────────────────────────────────

    /**
     * Clears the local session and navigates back to the sign-in screen.
     */
    fun signOut() {
        repository.signOut()
        _uiState.value = IamUiState(
            isSignedIn = false,
            role       = UserRole.GUEST.value
        )
        viewModelScope.launch {
            _events.send(AuthEvent.Navigate(AppRoutes.SIGN_IN))
        }
    }

    // ── Utilities ─────────────────────────────────────────────────────────────

    /**
     * Clears all current UI errors, e.g. after the user modifies a form field.
     */
    fun clearErrors() {
        _uiState.value = _uiState.value.copy(errors = emptyList())
    }
}