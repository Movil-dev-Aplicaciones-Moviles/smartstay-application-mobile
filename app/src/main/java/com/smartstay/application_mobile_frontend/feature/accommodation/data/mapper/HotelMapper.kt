package com.smartstay.application_mobile_frontend.feature.accommodation.data.mapper

import com.smartstay.application_mobile_frontend.feature.accommodation.data.remote.dto.HotelDto
import com.smartstay.application_mobile_frontend.feature.accommodation.domain.model.Hotel

fun HotelDto.toDomain(): Hotel {
    return Hotel(
        id = this.id ?: 0,
        hostId = this.hostId ?: 0,
        name = this.name ?: "Unknown Hotel",
        location = this.location ?: "No location provided",
        imageUrl = this.imageUrl ?: "",
        description = this.description,
        basePrice = this.basePrice ?: 0.0,
        type = this.type ?: "Accommodation",
        amenities = this.amenities ?: emptyList()
    )
}
