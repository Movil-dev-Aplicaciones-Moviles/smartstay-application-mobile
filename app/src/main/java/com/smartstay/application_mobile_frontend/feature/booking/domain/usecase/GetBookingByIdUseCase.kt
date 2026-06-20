// domain/usecase/GetBookingByIdUseCase.kt
package com.smartstay.application_mobile_frontend.feature.booking.domain.usecase

import com.smartstay.application_mobile_frontend.domain.model.bookings.Booking
import com.smartstay.application_mobile_frontend.feature.booking.domain.repository.BookingRepository
import javax.inject.Inject

class GetBookingByIdUseCase @Inject constructor(
    private val repository: BookingRepository
) {
    suspend operator fun invoke(bookingId: Int): Result<Booking> {
        return repository.getBookingById(bookingId)
    }
}