package com.smartstay.application_mobile_frontend.feature.accommodation.data.mapper

import com.smartstay.application_mobile_frontend.feature.accommodation.data.remote.dto.HotelDto
import com.smartstay.application_mobile_frontend.feature.accommodation.domain.model.Hotel

fun HotelDto.toDomain(): Hotel {
    return Hotel(
        id = this.id ?: 0,
        hostId = this.hostId ?: 0,
        name = this.name ?: "Unknown Hotel",
        address = this.address ?: "",
        city = this.city ?: "",
        country = this.country ?: "",
        location = this.location ?: "${this.address ?: ""}, ${this.city ?: ""}, ${this.country ?: ""}".trim(',',' '),
        imageUrl = this.imageUrl ?: "",
        description = this.description,
        type = this.type ?: "Accommodation",
        amenities = this.amenities ?: emptyList()
    )
}

fun Hotel.toDto(): HotelDto {
    return HotelDto(
        id = if (id == 0) null else id,
        hostId = hostId,
        name = name,
        address = address,
        city = city,
        country = country,
        location = location,
        imageUrl = imageUrl,
        description = description,
        type = type,
        amenities = amenities
    )
}
