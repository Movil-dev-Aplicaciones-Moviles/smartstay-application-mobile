package com.smartstay.application_mobile_frontend.feature.profile.data.dto

import com.smartstay.application_mobile_frontend.domain.model.profiles.Profile

data class ProfileDto(
    val id: Int,
    val fullName: String,
    val email: String,
    val streetAddress: String
) {
    fun toDomain(): Profile {
        val names = fullName.split(" ", limit = 2)
        val firstName = names.getOrNull(0) ?: ""
        val lastName = names.getOrNull(1) ?: ""

        return Profile(
            id = id,
            firstName = firstName,
            lastName = lastName,
            email = email,
            street = streetAddress,
            number = "",
            city = "",
            postalCode = "",
            country = ""
        )
    }
}

data class CreateProfileRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val street: String,
    val number: String,
    val city: String,
    val postalCode: String,
    val country: String
)