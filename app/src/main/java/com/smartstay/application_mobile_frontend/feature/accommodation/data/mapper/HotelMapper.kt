package com.smartstay.application_mobile_frontend.feature.accommodation.data.mapper

import com.smartstay.application_mobile_frontend.feature.accommodation.data.remote.dto.HotelDto
import com.smartstay.application_mobile_frontend.feature.accommodation.domain.model.Hotel

fun HotelDto.toDomain(): Hotel {
    return Hotel(
        id = this.id ?: 0,
        hostId = this.hostId ?: 0,
        name = this.name ?: "Unknown Hotel",
        address = this.address,
        city = this.city,
        country = this.country,
        imageUrl = this.imageUrl,
        description = this.description,
        type = this.type,
        amenities = this.amenities ?: emptyList(),
        lowestPrice = 0.0
    )
}
