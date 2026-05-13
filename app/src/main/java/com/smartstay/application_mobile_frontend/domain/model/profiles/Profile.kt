package com.smartstay.application_mobile_frontend.domain.model.profiles

data class Profile(
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

    fun fullName(): String {
        return "$firstName $lastName"
    }

    fun fullAddress(): String {
        return "$street $number, $city, $postalCode, $country"
    }
}