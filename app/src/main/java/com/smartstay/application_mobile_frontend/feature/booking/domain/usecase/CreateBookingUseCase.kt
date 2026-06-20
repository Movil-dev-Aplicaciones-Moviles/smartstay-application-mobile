// domain/usecase/CreateBookingUseCase.kt
package com.smartstay.application_mobile_frontend.feature.booking.domain.usecase

import com.smartstay.application_mobile_frontend.domain.model.bookings.Booking
import com.smartstay.application_mobile_frontend.feature.booking.domain.repository.BookingRepository
import java.time.LocalDate
import javax.inject.Inject

class CreateBookingUseCase @Inject constructor(
    private val repository: BookingRepository
) {
    suspend operator fun invoke(
        roomId: Int,
        guestName: String,
        guestEmail: String,
        checkInDate: LocalDate,
        checkOutDate: LocalDate,
        numberOfGuests: Int = 1,
        specialRequests: String? = null
    ): Result<Booking> {
        return repository.createBooking(
            roomId = roomId,
            guestName = guestName,
            guestEmail = guestEmail,
            checkInDate = checkInDate,
            checkOutDate = checkOutDate,
            numberOfGuests = numberOfGuests,
            specialRequests = specialRequests
        )
    }
}