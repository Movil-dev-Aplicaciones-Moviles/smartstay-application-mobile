package com.smartstay.application_mobile_frontend.feature.accommodation.domain.model

import com.smartstay.application_mobile_frontend.feature.accommodation.domain.model.RoomType

data class Room(
    val id: Int,
    val hotelId: Int,
    val roomTypeId: Int,
    val price: Double,
    val description: String,
    val amenities: List<String>,
    val roomType: RoomType?
) {

    fun hasAmenity(amenity: String): Boolean {
        return amenities.any {
            it.equals(amenity, ignoreCase = true)
        }
    }

    fun formattedPrice(): String {
        return "$${"%.2f".format(price)}"
    }
}


