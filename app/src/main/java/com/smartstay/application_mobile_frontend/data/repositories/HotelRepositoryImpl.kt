package com.smartstay.application_mobile_frontend.data.repositories

import com.smartstay.application_mobile_frontend.data.remote.accommodations.AccommodationApiService
import com.smartstay.application_mobile_frontend.data.mapper.toDomain
import com.smartstay.application_mobile_frontend.domain.model.accommodations.Hotel
import com.smartstay.application_mobile_frontend.domain.repository.accommodations.HotelRepository

class HotelRepositoryImpl(
    private val apiService: AccommodationApiService
) : HotelRepository {
    override suspend fun getHotels(): Result<List<Hotel>> {
        return try {
            val dtos = apiService.fetchHotels()
            Result.success(dtos.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getHotelById(id: Int): Result<Hotel> {
        TODO("Not yet implemented")
    }
}