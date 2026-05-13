package com.smartstay.application_mobile_frontend.domain.model.accommodations

data class Hotel(
    val id: Int,
    val hostId: Int,
    val name: String,
    val address: String,
    val city: String,
    val country: String,
    val imageUrl: String,
    val description: String,
    val type: String,
    val amenities: List<String>,
    val lowestPrice: Double
)