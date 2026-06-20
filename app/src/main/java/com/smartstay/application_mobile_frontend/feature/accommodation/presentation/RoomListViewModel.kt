package com.smartstay.application_mobile_frontend.feature.accommodation.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartstay.application_mobile_frontend.feature.accommodation.domain.model.Room
import com.smartstay.application_mobile_frontend.feature.accommodation.domain.usecase.GetRoomsByHotelUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RoomListUiState(
    val isLoading: Boolean = false,
    val rooms: List<Room> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class RoomListViewModel @Inject constructor(
    private val getRoomsByHotelUseCase: GetRoomsByHotelUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RoomListUiState())
    val uiState: StateFlow<RoomListUiState> = _uiState.asStateFlow()

    fun fetchRooms(hotelId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            getRoomsByHotelUseCase(hotelId).fold(
                onSuccess = { rooms ->
                    _uiState.update { it.copy(isLoading = false, rooms = rooms) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
            )
        }
    }
}
