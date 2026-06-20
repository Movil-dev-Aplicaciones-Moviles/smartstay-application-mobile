// domain/repository/BookingRepository.kt
package com.smartstay.application_mobile_frontend.feature.booking.domain.repository

import com.smartstay.application_mobile_frontend.domain.model.bookings.Booking
import java.time.LocalDate

interface BookingRepository {

    // ============ ADMIN ============

    /**
     * Obtiene todas las reservas → GetAllBookingsUseCase
     */
    suspend fun getBookings(): Result<List<Booking>>

    /**
     * Obtiene una reserva por su ID → GetBookingByIdUseCase
     */
    suspend fun getBookingById(id: Int): Result<Booking>

    /**
     * Obtiene reservas por ID de habitación → GetBookingsByRoomUseCase
     */
    suspend fun getBookingsByRoom(roomId: Int): Result<List<Booking>>

    /**
     * Confirma una reserva → ConfirmBookingUseCase
     */
    suspend fun confirmBooking(id: Int): Result<Booking>

    /**
     * Cancela una reserva → CancelBookingUseCase
     */
    suspend fun cancelBooking(id: Int): Result<Booking>

    // ============ GUEST ============

    /**
     * Crea una nueva reserva → CreateBookingUseCase
     */
    suspend fun createBooking(
        roomId: Int,
        guestName: String,
        guestEmail: String,
        checkInDate: LocalDate,
        checkOutDate: LocalDate,
        numberOfGuests: Int,
        specialRequests: String?
    ): Result<Booking>
}