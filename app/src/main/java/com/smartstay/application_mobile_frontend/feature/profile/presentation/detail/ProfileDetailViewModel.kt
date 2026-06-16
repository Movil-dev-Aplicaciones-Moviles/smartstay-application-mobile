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
                val profile = repository.getProfileById(profileId)
                _uiState.value = ProfileDetailUiState.Success(profile)
            } catch (e: Exception) {
                _uiState.value = ProfileDetailUiState.Error(e.message ?: "Error al cargar perfil")
            }
        }
    }

    // El método updateProfile iría aquí, enviando un PUT con los datos editados
    // ...
}