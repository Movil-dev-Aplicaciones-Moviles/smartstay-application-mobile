package com.smartstay.application_mobile_frontend.feature.accommodation.domain.repository

import com.smartstay.application_mobile_frontend.feature.accommodation.domain.model.Hotel

interface HotelRepository {
    suspend fun getHotels(): Result<List<Hotel>>
    suspend fun getHotelById(id: Int): Result<Hotel>
    suspend fun saveHotel(hotel: Hotel): Result<Hotel>
    suspend fun updateHotel(hotel: Hotel): Result<Hotel>
}
