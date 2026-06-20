package com.smartstay.application_mobile_frontend.feature.accommodation.domain.model

data class Room(
    val id: Int,
    val hotelId: Int,
    val roomTypeId: Int,
    val roomTypeName: String?,
    val price: Double,
    val description: String,
    val amenities: List<String>,
    val roomType: RoomType? = null
)
