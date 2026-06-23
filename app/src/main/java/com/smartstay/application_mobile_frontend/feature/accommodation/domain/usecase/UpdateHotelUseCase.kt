package com.smartstay.application_mobile_frontend.feature.accommodation.domain.usecase

import com.smartstay.application_mobile_frontend.feature.accommodation.domain.model.Hotel
import com.smartstay.application_mobile_frontend.feature.accommodation.domain.repository.HotelRepository
import javax.inject.Inject

class UpdateHotelUseCase @Inject constructor(
    private val repository: HotelRepository
) {
    suspend operator fun invoke(hotel: Hotel): Result<Hotel> {
        return repository.updateHotel(hotel)
    }
}
