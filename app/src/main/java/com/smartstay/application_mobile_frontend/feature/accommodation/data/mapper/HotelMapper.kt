package com.smartstay.application_mobile_frontend.feature.accommodation.data.mapper

import com.smartstay.application_mobile_frontend.feature.accommodation.data.remote.dto.HotelDto
import com.smartstay.application_mobile_frontend.feature.accommodation.domain.model.Hotel

fun HotelDto.toDomain(): Hotel {
    return Hotel(
        id = this.id,
        hostId = this.hostId,
        name = this.name,
        address = this.address,
        city = this.city,
        country = this.country,
        imageUrl = this.imageUrl,
        description = this.description,
        type = this.type,
        amenities = this.amenities,
        lowestPrice = 0.0
    )
}
