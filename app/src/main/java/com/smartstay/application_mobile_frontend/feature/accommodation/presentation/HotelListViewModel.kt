package com.smartstay.application_mobile_frontend.feature.accommodation.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartstay.application_mobile_frontend.feature.accommodation.domain.model.Hotel
import com.smartstay.application_mobile_frontend.feature.accommodation.domain.usecase.GetHotelsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class HotelListUiState(
    val isLoading: Boolean = false,
    val hotels: List<Hotel> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class HotelListViewModel @Inject constructor(
    private val getHotelsUseCase: GetHotelsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HotelListUiState())
    val uiState: StateFlow<HotelListUiState> = _uiState.asStateFlow()

    init {
        fetchAllHotels()
    }

    fun fetchAllHotels() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val result = withContext(Dispatchers.IO) {
                getHotelsUseCase()
            }

            result.fold(
                onSuccess = { hotels ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        hotels = hotels
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message?: "Unknow error"
                    )
                }
            )
        }
    }
}

