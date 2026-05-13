package com.smartstay.application_mobile_frontend.domain.model.bookings

import java.time.LocalDate

data class Booking(
    val id: Int,
    val roomId: Int,
    val guestName: String,
    val guestEmail: String,
    val checkInDate: LocalDate,
    val checkOutDate: LocalDate,
    val status: BookingStatus
) {

    fun isPending() = status == BookingStatus.PENDING

    fun isConfirmed() = status == BookingStatus.CONFIRMED

    fun isCancelled() = status == BookingStatus.CANCELLED

    fun isCompleted() = status == BookingStatus.COMPLETED
}