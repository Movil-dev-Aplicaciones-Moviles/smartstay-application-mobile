package com.smartstay.application_mobile_frontend.feature.accommodation.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartstay.application_mobile_frontend.feature.accommodation.domain.model.Hotel
import com.smartstay.application_mobile_frontend.feature.accommodation.domain.repository.HotelRepository
import com.smartstay.application_mobile_frontend.feature.accommodation.domain.usecase.UpdateHotelUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditHotelUiState(
    val isLoading: Boolean = false,
    val hotel: Hotel? = null,
    val isUpdateSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class EditHotelViewModel @Inject constructor(
    private val repository: HotelRepository,
    private val updateHotelUseCase: UpdateHotelUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditHotelUiState())
    val uiState: StateFlow<EditHotelUiState> = _uiState.asStateFlow()

    fun loadHotel(hotelId: Int) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            repository.getHotelById(hotelId).fold(
                onSuccess = { hotel ->
                    _uiState.update { it.copy(isLoading = false, hotel = hotel) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
            )
        }
    }

    fun updateHotel(
        hotelId: Int,
        hostId: Int,
        name: String,
        address: String,
        city: String,
        country: String,
        imageUrl: String,
        description: String,
        type: String,
        amenities: List<String>
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            val hotel = Hotel(
                id = hotelId,
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

            val result = updateHotelUseCase(hotel)
            
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, isUpdateSuccess = true) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
            )
        }
    }
}
