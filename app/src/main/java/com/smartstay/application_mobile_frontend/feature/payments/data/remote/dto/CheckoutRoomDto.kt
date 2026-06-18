package com.smartstay.application_mobile_frontend.feature.payments.data.remote.dto

data class CheckoutRoomDto(
    val id: Int,
    val hotelId: Int,
    val roomTypeId: Int,
    val roomTypeName: String?,
    val price: Double,
    val description: String?,
    val amenities: List<String>?
)
