package com.smartstay.application_mobile_frontend.feature.accommodation.domain.model

data class Hotel(
    val id: Int,
    val hostId: Int,
    val name: String,
    val location: String,
    val imageUrl: String,
    val description: String?,
    val basePrice: Double,
    val type: String,
    val amenities: List<String>
)
