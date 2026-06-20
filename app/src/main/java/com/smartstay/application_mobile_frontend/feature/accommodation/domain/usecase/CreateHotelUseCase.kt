package com.smartstay.application_mobile_frontend.feature.accommodation.domain.usecase

import com.smartstay.application_mobile_frontend.feature.accommodation.domain.model.Hotel
import com.smartstay.application_mobile_frontend.feature.accommodation.domain.repository.HotelRepository
import javax.inject.Inject

class CreateHotelUseCase @Inject constructor(
    private val repository: HotelRepository
) {
    suspend operator fun invoke(hotel: Hotel): Result<Hotel> {
        // Regla de negocio: Validar que el nombre no esté vacío antes de enviar al repo
        if (hotel.name.isBlank()) {
            return Result.failure(IllegalArgumentException("El nombre del hotel no puede estar vacío"))
        }
        return repository.saveHotel(hotel)
    }
}
