package com.smartstay.application_mobile_frontend.feature.profile.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartstay.application_mobile_frontend.core.datastore.TokenManager
import com.smartstay.application_mobile_frontend.feature.iam.domain.model.UserPermissions
import com.smartstay.application_mobile_frontend.feature.profile.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class ProfileDetailViewModel @Inject constructor(
    private val repository: ProfileRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    val currentEmail = runBlocking { tokenManager.getUsername() } ?: ""
    val permissions = UserPermissions(runBlocking { tokenManager.getRole() } ?: "")

    private val _uiState = MutableStateFlow<ProfileDetailUiState>(ProfileDetailUiState.Idle)
    val uiState: StateFlow<ProfileDetailUiState> = _uiState.asStateFlow()

    fun loadProfile(profileId: Int) {
        _uiState.value = ProfileDetailUiState.Loading
        viewModelScope.launch {
            try {
                // INTENTO 1: Si es un administrador, intentamos traer el directorio completo para cruzar datos
                if (permissions.canManageUsers) {
                    val allProfiles = repository.getProfiles()
                    val targetProfile =
                        allProfiles.find { it.email.equals(currentEmail, ignoreCase = true) }

                    if (targetProfile != null) {
                        _uiState.value = ProfileDetailUiState.Success(targetProfile)
                        return@launch
                    }
                }
                val profile = repository.getProfileById(profileId)
                _uiState.value = ProfileDetailUiState.Success(profile)

            } catch (e: Exception) {
                if (!permissions.canManageUsers && currentEmail.isNotBlank()) {
                    val fallbackProfile =
                        com.smartstay.application_mobile_frontend.domain.model.profiles.Profile(
                            id = profileId,
                            firstName = currentEmail.substringBefore('@')
                                .replaceFirstChar { it.uppercase() },
                            lastName = currentEmail,
                            email = currentEmail,
                            street = "-",
                            number = "-",
                            city = "-",
                            postalCode = "-",
                            country = "-"
                        )
                    _uiState.value = ProfileDetailUiState.Success(fallbackProfile)
                } else {
                    _uiState.value = ProfileDetailUiState.Error(
                        "Tu perfil biográfico aún no ha sido sincronizado en el sistema. Contacta a tu administrador corporativo."
                    )
                }
            }
        }
    }
    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                tokenManager.clearSession()
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = ProfileDetailUiState.Error("Error al cerrar sesión")
            }
        }
    }
}