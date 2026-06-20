package com.smartstay.application_mobile_frontend.feature.profile.presentation.detail

import com.smartstay.application_mobile_frontend.domain.model.profiles.Profile

sealed class ProfileDetailUiState {
    data object Idle : ProfileDetailUiState()
    data object Loading : ProfileDetailUiState()
    data class Success(val profile: Profile) : ProfileDetailUiState()
    data object Saving : ProfileDetailUiState()
    data object SaveSuccess : ProfileDetailUiState()
    data class Error(val message: String) : ProfileDetailUiState()
}