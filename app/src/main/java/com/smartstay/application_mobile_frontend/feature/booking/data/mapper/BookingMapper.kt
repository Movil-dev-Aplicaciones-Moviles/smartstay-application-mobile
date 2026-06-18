// feature/booking/data/mapper/BookingMapper.kt
package com.smartstay.application_mobile_frontend.feature.booking.data.mapper

import com.smartstay.application_mobile_frontend.domain.model.bookings.Booking
import com.smartstay.application_mobile_frontend.domain.model.bookings.BookingStatus
import com.smartstay.application_mobile_frontend.feature.booking.data.remote.dto.BookingDto
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

fun BookingDto.toDomain(): Booking {
    return Booking(
        id = this.id,
        roomId = this.roomId,
        guestName = this.guestName,
        guestEmail = this.guestEmail,
        checkInDate = LocalDate.parse(this.checkInDate, dateFormatter),
        checkOutDate = LocalDate.parse(this.checkOutDate, dateFormatter),
        status = BookingStatus.valueOf(this.status)
    )
}

fun List<BookingDto>.toDomainList(): List<Booking> {
    return this.map { it.toDomain() }
}