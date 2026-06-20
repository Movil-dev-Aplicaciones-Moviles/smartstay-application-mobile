package com.smartstay.application_mobile_frontend.feature.accommodation.data.remote.dto

import com.google.gson.annotations.SerializedName

data class HotelDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("hostId") val hostId: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("address") val address: String?, // Usado en POST/PUT
    @SerializedName("location") val location: String?, // Usado en GET
    @SerializedName("city") val city: String?,
    @SerializedName("country") val country: String?,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("basePrice") val basePrice: Double?,
    @SerializedName("type") val type: String?,
    @SerializedName("amenities") val amenities: List<String>?
)
