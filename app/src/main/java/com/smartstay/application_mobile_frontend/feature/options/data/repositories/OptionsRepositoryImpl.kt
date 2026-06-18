package com.smartstay.application_mobile_frontend.feature.options.data.repositories

import com.smartstay.application_mobile_frontend.feature.options.data.mapper.toDomain
import com.smartstay.application_mobile_frontend.feature.options.data.mapper.toDto
import com.smartstay.application_mobile_frontend.feature.options.data.remote.OptionsApiService
import com.smartstay.application_mobile_frontend.feature.options.domain.model.Amenity
import com.smartstay.application_mobile_frontend.feature.options.domain.model.HotelCategory
import com.smartstay.application_mobile_frontend.feature.options.domain.repository.OptionsRepository
import javax.inject.Inject

class OptionsRepositoryImpl @Inject constructor(
    private val apiService: OptionsApiService
) : OptionsRepository {

    override suspend fun getAmenities(): Result<List<String>> {
        return try {
            val response = apiService.getAmenities()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveAmenity(amenity: Amenity): Result<Amenity> {
        return try {
            val response = apiService.saveAmenity(amenity.toDto())
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateAmenity(amenity: Amenity): Result<Amenity> {
        return try {
            val response = apiService.updateAmenity(amenity.toDto())
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getHotelCategories(): Result<List<String>> {
        return try {
            val response = apiService.getHotelCategories()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveHotelCategory(category: HotelCategory): Result<HotelCategory> {
        return try {
            val response = apiService.saveHotelCategory(category.toDto())
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
