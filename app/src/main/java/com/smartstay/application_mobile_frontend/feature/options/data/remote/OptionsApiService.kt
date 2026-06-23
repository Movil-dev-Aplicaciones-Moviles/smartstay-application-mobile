package com.smartstay.application_mobile_frontend.feature.options.data.remote

import com.smartstay.application_mobile_frontend.feature.options.data.remote.dto.AmenityDto
import com.smartstay.application_mobile_frontend.feature.options.data.remote.dto.HotelCategoryDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface OptionsApiService {
    @GET("accommodations/options/amenities")
    suspend fun getAmenities(): List<String>

    @POST("accommodations/options/amenities")
    suspend fun saveAmenity(@Body amenity: AmenityDto): AmenityDto

    @PUT("accommodations/options/amenities")
    suspend fun updateAmenity(@Body amenity: AmenityDto): AmenityDto

    @GET("accommodations/options/categories")
    suspend fun getHotelCategories(): List<String>

    @POST("accommodations/options/categories")
    suspend fun saveHotelCategory(@Body category: HotelCategoryDto): HotelCategoryDto
}
