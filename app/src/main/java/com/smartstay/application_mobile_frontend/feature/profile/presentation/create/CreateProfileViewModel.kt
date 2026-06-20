package com.smartstay.application_mobile_frontend.feature.profile.presentation.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartstay.application_mobile_frontend.feature.profile.data.dto.CreateProfileRequest
import com.smartstay.application_mobile_frontend.feature.profile.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// 1. Definimos los estados posibles de la pantalla
sealed class CreateProfileUiState {
    data object Idle : CreateProfileUiState()
    data object Loading : CreateProfileUiState()
    data object Success : CreateProfileUiState()
    data class Error(val message: String) : CreateProfileUiState()
}

// 2. El ViewModel que controla la lógica
@HiltViewModel
class CreateProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository // Asegúrate de que el nombre coincida con tu repositorio
) : ViewModel() {

    private val _uiState = MutableStateFlow<CreateProfileUiState>(CreateProfileUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun createProfile(request: CreateProfileRequest) {
        _uiState.value = CreateProfileUiState.Loading
        viewModelScope.launch {
            try {
                // Disparamos la petición POST al backend
                profileRepository.createProfile(request)
                _uiState.value = CreateProfileUiState.Success
            } catch (e: Exception) {
                _uiState.value = CreateProfileUiState.Error(e.message ?: "Error al guardar la ficha biográfica")
            }
        }
    }
}