package com.smartstay.application_mobile_frontend.feature.accommodation.data.repositories

import com.smartstay.application_mobile_frontend.feature.accommodation.data.mapper.toDomain
import com.smartstay.application_mobile_frontend.feature.accommodation.data.mapper.toDto
import com.smartstay.application_mobile_frontend.feature.accommodation.data.remote.AccommodationApiService
import com.smartstay.application_mobile_frontend.feature.accommodation.domain.model.Hotel
import com.smartstay.application_mobile_frontend.feature.accommodation.domain.repository.HotelRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class HotelRepositoryImpl @Inject constructor(
    private val apiService: AccommodationApiService
) : HotelRepository {
    override suspend fun getHotels(): Result<List<Hotel>> = withContext(Dispatchers.IO) {
        try {
            val dtos = apiService.fetchHotels()
            Result.success(dtos.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getHotelById(id: Int): Result<Hotel> = withContext(Dispatchers.IO) {
        try {
            val dto = apiService.getHotelById(id)
            Result.success(dto.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveHotel(hotel: Hotel): Result<Hotel> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.createHotel(hotel.toDto())
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateHotel(hotel: Hotel): Result<Hotel> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.updateHotel(hotel.id, hotel.toDto())
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
