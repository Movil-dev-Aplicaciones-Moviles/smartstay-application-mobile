package com.smartstay.application_mobile_frontend.feature.accommodation.data.repositories

import com.smartstay.application_mobile_frontend.feature.accommodation.data.mapper.toDomain
import com.smartstay.application_mobile_frontend.feature.accommodation.data.mapper.toDto
import com.smartstay.application_mobile_frontend.feature.accommodation.data.remote.AccommodationApiService
import com.smartstay.application_mobile_frontend.feature.accommodation.domain.model.Room
import com.smartstay.application_mobile_frontend.feature.accommodation.domain.model.RoomType
import com.smartstay.application_mobile_frontend.feature.accommodation.domain.repository.RoomRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RoomRepositoryImpl @Inject constructor(
    private val apiService: AccommodationApiService
) : RoomRepository {

    override suspend fun getRooms(hotelId: Int?): Result<List<Room>> = withContext(Dispatchers.IO) {
        try {
            val dtos = apiService.getRooms(hotelId)
            Result.success(dtos.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRoomById(id: Int): Result<Room> = withContext(Dispatchers.IO) {
        try {
            val dto = apiService.getRoomById(id)
            Result.success(dto.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveRoom(room: Room): Result<Room> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.createRoom(room.toDto())
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateRoom(room: Room): Result<Room> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.updateRoom(room.id, room.toDto())
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteRoom(id: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            apiService.deleteRoom(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRoomTypes(): Result<List<RoomType>> = withContext(Dispatchers.IO) {
        try {
            val dtos = apiService.getRoomTypes()
            Result.success(dtos.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
