package com.smartstay.application_mobile_frontend.feature.accommodation.domain.repository

import com.smartstay.application_mobile_frontend.feature.accommodation.domain.model.Room
import com.smartstay.application_mobile_frontend.feature.accommodation.domain.model.RoomType

interface RoomRepository {
    suspend fun getRooms(hotelId: Int? = null): Result<List<Room>>
    suspend fun getRoomById(id: Int): Result<Room>
    suspend fun saveRoom(room: Room): Result<Room>
    suspend fun updateRoom(room: Room): Result<Room>
    suspend fun deleteRoom(id: Int): Result<Unit>
    suspend fun getRoomTypes(): Result<List<RoomType>>
}
