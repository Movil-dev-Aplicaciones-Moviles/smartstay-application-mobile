package com.smartstay.application_mobile_frontend.feature.accommodation.data.mapper

import com.smartstay.application_mobile_frontend.feature.accommodation.data.remote.dto.RoomDto
import com.smartstay.application_mobile_frontend.feature.accommodation.data.remote.dto.RoomTypeDto
import com.smartstay.application_mobile_frontend.feature.accommodation.domain.model.Room
import com.smartstay.application_mobile_frontend.feature.accommodation.domain.model.RoomType

fun RoomDto.toDomain(): Room {
    return Room(
        id = this.id ?: 0,
        hotelId = this.hotelId ?: 0,
        roomTypeId = this.roomTypeId ?: 0,
        price = this.price ?: 0.0,
        description = this.description ?: "",
        amenities = this.amenities ?: emptyList(),
        roomType = this.roomType?.toDomain()
    )
}

fun RoomTypeDto.toDomain(): RoomType {
    return RoomType(
        id = this.id ?: 0,
        name = this.name ?: "Standard",
        description = this.description ?: ""
    )
}

fun Room.toDto(): RoomDto {
    return RoomDto(
        id = if (id == 0) null else id,
        hotelId = hotelId,
        roomTypeId = roomTypeId,
        price = price,
        description = description,
        amenities = amenities,
        roomType = roomType?.toDto()
    )
}

fun RoomType.toDto(): RoomTypeDto {
    return RoomTypeDto(
        id = if (id == 0) null else id,
        name = name,
        description = description
    )
}
