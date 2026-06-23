package com.smartstay.application_mobile_frontend.feature.options.data.mapper

import com.smartstay.application_mobile_frontend.feature.options.data.remote.dto.AmenityDto
import com.smartstay.application_mobile_frontend.feature.options.data.remote.dto.HotelCategoryDto
import com.smartstay.application_mobile_frontend.feature.options.domain.model.Amenity
import com.smartstay.application_mobile_frontend.feature.options.domain.model.HotelCategory

fun AmenityDto.toDomain(): Amenity {
    return Amenity(
        id = id,
        name = name,
        description = description,
        iconUrl = iconUrl
    )
}

fun Amenity.toDto(): AmenityDto {
    return AmenityDto(
        id = id,
        name = name,
        description = description,
        iconUrl = iconUrl
    )
}

fun HotelCategoryDto.toDomain(): HotelCategory {
    return HotelCategory(
        id = id,
        name = name,
        description = description
    )
}

fun HotelCategory.toDto(): HotelCategoryDto {
    return HotelCategoryDto(
        id = id,
        name = name,
        description = description
    )
}
