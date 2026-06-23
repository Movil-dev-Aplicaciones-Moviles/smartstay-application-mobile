package com.smartstay.application_mobile_frontend.feature.accommodation.domain.usecase

import com.smartstay.application_mobile_frontend.feature.accommodation.domain.model.Room
import com.smartstay.application_mobile_frontend.feature.accommodation.domain.repository.RoomRepository
import javax.inject.Inject

class GetRoomsByHotelUseCase @Inject constructor(
    private val repository: RoomRepository
) {
    suspend operator fun invoke(hotelId: Int): Result<List<Room>> {
        return repository.getRooms(hotelId)
    }
}
