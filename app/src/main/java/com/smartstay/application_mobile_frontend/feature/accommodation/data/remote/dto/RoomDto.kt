package com.smartstay.application_mobile_frontend.feature.accommodation.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RoomDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("hotelId") val hotelId: Int?,
    @SerializedName("roomTypeId") val roomTypeId: Int?,
    @SerializedName("price") val price: Double?,
    @SerializedName("description") val description: String?,
    @SerializedName("amenities") val amenities: List<String>?,
    @SerializedName("roomType") val roomType: RoomTypeDto?
)

data class RoomTypeDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("description") val description: String?
)
