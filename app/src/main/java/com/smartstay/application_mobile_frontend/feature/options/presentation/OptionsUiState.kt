package com.smartstay.application_mobile_frontend.feature.options.presentation

import com.smartstay.application_mobile_frontend.feature.options.domain.model.Amenity
import com.smartstay.application_mobile_frontend.feature.options.domain.model.HotelCategory

data class OptionsUiState(
    val isLoading: Boolean = false,
    val amenities: List<Amenity> = emptyList(),
    val categories: List<HotelCategory> = emptyList(),
    val errorMessage: String? = null
)
