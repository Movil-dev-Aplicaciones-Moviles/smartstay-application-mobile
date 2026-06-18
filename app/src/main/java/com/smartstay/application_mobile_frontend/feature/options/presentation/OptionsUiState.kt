package com.smartstay.application_mobile_frontend.feature.options.presentation

data class OptionsUiState(
    val isLoading: Boolean = false,
    val amenities: List<String> = emptyList(),
    val categories: List<String> = emptyList(),
    val errorMessage: String? = null
)
