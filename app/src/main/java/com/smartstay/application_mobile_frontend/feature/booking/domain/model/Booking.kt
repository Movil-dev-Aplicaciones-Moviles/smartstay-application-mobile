// domain/model/Booking.kt
package com.smartstay.application_mobile_frontend.feature.booking.domain.model

import com.smartstay.application_mobile_frontend.domain.model.bookings.BookingStatus
import java.time.LocalDate

data class Booking(
    val id: Int,
    val roomId: Int,
    val guestName: String,
    val guestEmail: String,
    val checkInDate: LocalDate,
    val checkOutDate: LocalDate,
    val status: com.smartstay.application_mobile_frontend.domain.model.bookings.BookingStatus
) {

    fun isPending() = status == com.smartstay.application_mobile_frontend.domain.model.bookings.BookingStatus.PENDING

    fun isConfirmed() = status == com.smartstay.application_mobile_frontend.domain.model.bookings.BookingStatus.CONFIRMED

    fun isCancelled() = status == com.smartstay.application_mobile_frontend.domain.model.bookings.BookingStatus.CANCELLED

    fun isCompleted() = status == BookingStatus.COMPLETED
}

