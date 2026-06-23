package com.smartstay.application_mobile_frontend.feature.accommodation.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartstay.application_mobile_frontend.feature.accommodation.domain.model.Room
import com.smartstay.application_mobile_frontend.feature.accommodation.domain.model.RoomType
import com.smartstay.application_mobile_frontend.feature.accommodation.domain.repository.RoomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddRoomUiState(
    val roomTypes: List<RoomType> = emptyList(),
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class AddRoomViewModel @Inject constructor(
    private val repository: RoomRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddRoomUiState())
    val uiState: StateFlow<AddRoomUiState> = _uiState.asStateFlow()

    init {
        loadRoomTypes()
    }

    private fun loadRoomTypes() {
        viewModelScope.launch {
            repository.getRoomTypes().onSuccess { types ->
                _uiState.update { it.copy(roomTypes = types) }
            }
        }
    }

    fun createRoom(
        hotelId: Int,
        roomTypeId: Int,
        price: Double,
        description: String,
        amenities: List<String>
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            val room = Room(
                id = 0,
                hotelId = hotelId,
                roomTypeId = roomTypeId,
                roomTypeName = null,
                price = price,
                description = description,
                amenities = amenities
            )

            repository.saveRoom(room).fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
            )
        }
    }
}
