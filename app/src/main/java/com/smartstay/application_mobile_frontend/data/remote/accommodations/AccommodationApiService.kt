package com.smartstay.application_mobile_frontend.data.remote.accommodations

import com.smartstay.application_mobile_frontend.data.remote.accommodations.dto.HotelDto
import retrofit2.http.GET
import retrofit2.http.Path

interface AccommodationApiService {
    @GET("api/v1/accommodations/hotels")
    suspend fun fetchHotels(): List<HotelDto>
}