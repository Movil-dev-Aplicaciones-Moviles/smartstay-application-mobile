package com.smartstay.application_mobile_frontend.feature.accommodation.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartstay.application_mobile_frontend.core.datastore.TokenManager
import com.smartstay.application_mobile_frontend.feature.accommodation.domain.model.Hotel
import com.smartstay.application_mobile_frontend.feature.accommodation.domain.usecase.CreateHotelUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

data class AddHotelUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class AddHotelViewModel @Inject constructor(
    private val createHotelUseCase: CreateHotelUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddHotelUiState())
    val uiState: StateFlow<AddHotelUiState> = _uiState.asStateFlow()

    fun createHotel(
        name: String,
        address: String,
        city: String,
        country: String,
        imageUrl: String,
        description: String,
        basePrice: Double,
        type: String,
        amenities: List<String>
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            val hostId = runBlocking { tokenManager.getUserId() } ?: 0
            
            val hotel = Hotel(
                id = 0,
                hostId = hostId,
                name = name,
                address = address,
                city = city,
                country = country,
                location = "$address, $city, $country",
                imageUrl = imageUrl,
                description = description,

                type = type,
                amenities = amenities
            )

            val result = createHotelUseCase(hotel)
            
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
            )
        }
    }
    
    fun resetState() {
        _uiState.value = AddHotelUiState()
    }
}
