package com.smartstay.application_mobile_frontend.feature.profile.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartstay.application_mobile_frontend.domain.model.profiles.Profile
import com.smartstay.application_mobile_frontend.feature.profile.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ProfileListUiState {
    data object Loading : ProfileListUiState()
    data class Success(val profiles: List<Profile>) : ProfileListUiState()
    data class Error(val message: String) : ProfileListUiState()
}

@HiltViewModel
class ProfileListViewModel @Inject constructor(
    private val repository: ProfileRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<ProfileListUiState>(ProfileListUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init { loadProfiles() }

    fun loadProfiles() {
        _uiState.value = ProfileListUiState.Loading
        viewModelScope.launch {
            try {
                _uiState.value = ProfileListUiState.Success(repository.getProfiles())
            } catch (e: Exception) {
                _uiState.value = ProfileListUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }
}