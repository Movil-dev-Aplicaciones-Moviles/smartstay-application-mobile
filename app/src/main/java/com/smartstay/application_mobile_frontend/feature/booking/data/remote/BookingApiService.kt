// feature/booking/data/remote/BookingApiService.kt
package com.smartstay.application_mobile_frontend.feature.booking.data.remote

import com.smartstay.application_mobile_frontend.feature.booking.data.remote.dto.BookingDto
import com.smartstay.application_mobile_frontend.feature.booking.data.remote.dto.CreateBookingRequest
import retrofit2.http.*

interface BookingApiService {

    /**
     * Obtiene todas las reservas (Admin)
     * GET /api/v1/bookings
     */
    @GET("api/v1/bookings")
    suspend fun getAllBookings(): List<BookingDto>

    /**
     * Obtiene una reserva por ID (Admin)
     * GET /api/v1/bookings/{id}
     */
    @GET("api/v1/bookings/{id}")
    suspend fun getBookingById(
        @Path("id") bookingId: Int
    ): BookingDto

    /**
     * Crea una nueva reserva (Guest / Admin)
     * POST /api/v1/bookings
     */
    @POST("api/v1/bookings")
    suspend fun createBooking(
        @Body request: CreateBookingRequest
    ): BookingDto

    /**
     * Confirma una reserva (Admin)
     * POST /api/v1/bookings/{id}/confirm
     */
    @POST("api/v1/bookings/{id}/confirm")
    suspend fun confirmBooking(
        @Path("id") bookingId: Int
    ): BookingDto

    /**
     * Cancela una reserva (Admin)
     * POST /api/v1/bookings/{id}/cancel
     */
    @POST("api/v1/bookings/{id}/cancel")
    suspend fun cancelBooking(
        @Path("id") bookingId: Int
    ): BookingDto

    /**
     * Obtiene reservas por ID de habitación (Admin)
     * GET /api/v1/bookings/room/{roomId}
     */
    @GET("api/v1/bookings/room/{roomId}")
    suspend fun getBookingsByRoom(
        @Path("roomId") roomId: Int
    ): List<BookingDto>
}