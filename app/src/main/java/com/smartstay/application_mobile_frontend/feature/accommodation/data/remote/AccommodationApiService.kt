package com.smartstay.application_mobile_frontend.feature.accommodation.data.remote

import com.smartstay.application_mobile_frontend.feature.accommodation.data.remote.dto.HotelDto
import retrofit2.http.GET

interface AccommodationApiService {
    @GET("hotels")
    suspend fun fetchHotels(): List<HotelDto>
}

