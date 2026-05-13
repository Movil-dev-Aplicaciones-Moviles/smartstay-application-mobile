package com.smartstay.application_mobile_frontend.domain.usecase.accommodations

import com.smartstay.application_mobile_frontend.domain.model.accommodations.Hotel
import com.smartstay.application_mobile_frontend.domain.repository.accommodations.HotelRepository
class GetHotelsUseCase(
    private val repository: HotelRepository
) {
    suspend operator fun invoke(): Result<List<Hotel>> {
        return repository.getHotels()
    }
}