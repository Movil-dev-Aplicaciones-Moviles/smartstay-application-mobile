package com.smartstay.application_mobile_frontend.domain.repository.accommodations

import com.smartstay.application_mobile_frontend.domain.model.accommodations.Hotel

interface HotelRepository {
    suspend fun getHotels(): Result<List<Hotel>>
    suspend fun getHotelById(id: Int): Result<Hotel>
}