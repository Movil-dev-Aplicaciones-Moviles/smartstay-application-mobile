package com.smartstay.application_mobile_frontend.feature.options.domain.repository

import com.smartstay.application_mobile_frontend.feature.options.domain.model.Amenity
import com.smartstay.application_mobile_frontend.feature.options.domain.model.HotelCategory

interface OptionsRepository {
    suspend fun getAmenities(): Result<List<Amenity>>
    suspend fun saveAmenity(amenity: Amenity): Result<Amenity>
    suspend fun updateAmenity(amenity: Amenity): Result<Amenity>
    
    suspend fun getHotelCategories(): Result<List<HotelCategory>>
    suspend fun saveHotelCategory(category: HotelCategory): Result<HotelCategory>
}
