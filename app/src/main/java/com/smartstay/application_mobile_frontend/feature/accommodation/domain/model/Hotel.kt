package com.smartstay.application_mobile_frontend.feature.accommodation.domain.model

data class Hotel(
    val id: Int,
    val hostId: Int,
    val name: String,
    val address: String,
    val city: String,
    val country: String,
    val location: String, // Combined string for display
    val imageUrl: String,
    val description: String?,
    val type: String,
    val amenities: List<String>
)
