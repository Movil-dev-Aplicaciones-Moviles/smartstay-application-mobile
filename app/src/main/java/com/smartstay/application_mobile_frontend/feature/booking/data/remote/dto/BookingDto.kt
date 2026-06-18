// feature/booking/data/remote/dto/BookingDto.kt
package com.smartstay.application_mobile_frontend.feature.booking.data.remote.dto

data class BookingDto(
    val id: Int,
    val roomId: Int,
    val guestName: String,
    val guestEmail: String,
    val checkInDate: String,    // "yyyy-MM-dd"
    val checkOutDate: String,   // "yyyy-MM-dd"
    val status: String,         // "PENDING", "CONFIRMED", "CANCELLED", "COMPLETED"
    val numberOfGuests: Int,
    val totalAmount: Double,
    val specialRequests: String? = null,
    val createdAt: String? = null,
    val roomNumber: String? = null,
    val roomType: String? = null
)

// DTO para crear una reserva (request)
data class CreateBookingRequest(
    val roomId: Int,
    val checkInDate: String,
    val checkOutDate: String,
    val numberOfGuests: Int,
    val specialRequests: String? = null
)