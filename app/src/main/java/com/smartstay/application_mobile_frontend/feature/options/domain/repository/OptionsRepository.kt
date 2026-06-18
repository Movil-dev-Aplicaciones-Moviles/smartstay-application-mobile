package com.smartstay.application_mobile_frontend.feature.options.domain.repository

import com.smartstay.application_mobile_frontend.feature.options.domain.model.Amenity
import com.smartstay.application_mobile_frontend.feature.options.domain.model.HotelCategory

interface OptionsRepository {
    suspend fun getAmenities(): Result<List<String>>
    suspend fun saveAmenity(amenity: Amenity): Result<Amenity>
    suspend fun updateAmenity(amenity: Amenity): Result<Amenity>
    
    suspend fun getHotelCategories(): Result<List<String>>
    suspend fun saveHotelCategory(category: HotelCategory): Result<HotelCategory>
}
