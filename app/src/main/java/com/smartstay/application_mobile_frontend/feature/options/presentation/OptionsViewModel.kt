package com.smartstay.application_mobile_frontend.feature.options.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartstay.application_mobile_frontend.feature.options.domain.repository.OptionsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OptionsViewModel @Inject constructor(
    private val repository: OptionsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OptionsUiState())
    val uiState: StateFlow<OptionsUiState> = _uiState.asStateFlow()

    init {
        loadOptions()
    }

    fun loadOptions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val amenitiesResult = repository.getAmenities()
            val categoriesResult = repository.getHotelCategories()

            if (amenitiesResult.isSuccess && categoriesResult.isSuccess) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        amenities = amenitiesResult.getOrNull() ?: emptyList(),
                        categories = categoriesResult.getOrNull() ?: emptyList()
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al cargar las opciones de alojamiento"
                    )
                }
            }
        }
    }
}
