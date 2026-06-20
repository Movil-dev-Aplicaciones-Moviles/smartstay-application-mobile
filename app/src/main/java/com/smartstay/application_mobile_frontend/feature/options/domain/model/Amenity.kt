package com.smartstay.application_mobile_frontend.feature.options.domain.model

data class Amenity(
    val id: Int,
    val name: String,
    val description: String,
    val iconUrl: String? = null
)
