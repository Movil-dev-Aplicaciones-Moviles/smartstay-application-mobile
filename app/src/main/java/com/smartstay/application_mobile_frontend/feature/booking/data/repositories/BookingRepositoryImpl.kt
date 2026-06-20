// feature/booking/data/repository/BookingRepositoryImpl.kt
package com.smartstay.application_mobile_frontend.feature.booking.data.repositories

import com.smartstay.application_mobile_frontend.domain.model.bookings.Booking

import com.smartstay.application_mobile_frontend.feature.booking.data.mapper.toDomain
import com.smartstay.application_mobile_frontend.feature.booking.data.mapper.toDomainList
import com.smartstay.application_mobile_frontend.feature.booking.data.remote.BookingApiService
import com.smartstay.application_mobile_frontend.feature.booking.data.remote.dto.CreateBookingRequest
import com.smartstay.application_mobile_frontend.feature.booking.domain.repository.BookingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class BookingRepositoryImpl @Inject constructor(
    private val apiService: BookingApiService,
) : BookingRepository {

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    // ============ ADMIN ============

    override suspend fun getBookings(): Result<List<Booking>> {
        return withContext(Dispatchers.IO) {
            try {
                val dtos = apiService.getAllBookings()
                Result.success(dtos.toDomainList())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun getBookingById(id: Int): Result<Booking> {
        return withContext(Dispatchers.IO) {
            try {
                val dto = apiService.getBookingById(id)
                Result.success(dto.toDomain())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun confirmBooking(id: Int): Result<Booking> {
        return withContext(Dispatchers.IO) {
            try {
                val dto = apiService.confirmBooking(id)
                Result.success(dto.toDomain())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun cancelBooking(id: Int): Result<Booking> {
        return withContext(Dispatchers.IO) {
            try {
                val dto = apiService.cancelBooking(id)
                Result.success(dto.toDomain())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun getBookingsByRoom(roomId: Int): Result<List<Booking>> {
        return withContext(Dispatchers.IO) {
            try {
                val dtos = apiService.getBookingsByRoom(roomId)
                Result.success(dtos.toDomainList())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // ============ GUEST ============

    override suspend fun createBooking(
        roomId: Int,
        guestName: String,
        guestEmail: String,
        checkInDate: LocalDate,
        checkOutDate: LocalDate,
        numberOfGuests: Int,
        specialRequests: String?,
    ): Result<Booking> {
        return withContext(Dispatchers.IO) {
            try {
                val request = CreateBookingRequest(
                    roomId = roomId,
                    checkInDate = checkInDate.format(dateFormatter),
                    checkOutDate = checkOutDate.format(dateFormatter),
                    numberOfGuests = numberOfGuests,
                    specialRequests = specialRequests
                )
                val dto = apiService.createBooking(request)
                Result.success(dto.toDomain())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}