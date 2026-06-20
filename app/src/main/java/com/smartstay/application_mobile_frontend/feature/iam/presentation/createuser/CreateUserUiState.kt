package com.smartstay.application_mobile_frontend.feature.iam.presentation.createuser

import com.smartstay.application_mobile_frontend.feature.iam.domain.model.User

sealed class CreateUserUiState {
    data object Idle : CreateUserUiState()
    data object Loading : CreateUserUiState()
    data object Success : CreateUserUiState()
    data class Error(val message: String) : CreateUserUiState()
}