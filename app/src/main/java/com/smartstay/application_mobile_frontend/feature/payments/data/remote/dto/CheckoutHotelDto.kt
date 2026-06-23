package com.smartstay.application_mobile_frontend.feature.payments.data.remote.dto

data class CheckoutHotelDto(
    val id: Int,
    val name: String,
    val location: String?,
    val basePrice: Double?
)
