package com.smartstay.application_mobile_frontend.feature.profile.data.dto

import com.smartstay.application_mobile_frontend.domain.model.profiles.Profile

data class ProfileDto(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val street: String,
    val number: String,
    val city: String,
    val postalCode: String,
    val country: String
) {
    fun toDomain(): Profile = Profile(
        id = id, firstName = firstName, lastName = lastName,
        email = email, street = street, number = number,
        city = city, postalCode = postalCode, country = country
    )
}

data class CreateProfileRequest(
    val firstName: String, val lastName: String, val email: String,
    val street: String, val number: String, val city: String,
    val postalCode: String, val country: String
)