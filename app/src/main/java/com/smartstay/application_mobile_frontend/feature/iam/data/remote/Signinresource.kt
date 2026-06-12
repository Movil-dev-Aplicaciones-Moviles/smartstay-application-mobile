package com.smartstay.application_mobile_frontend.feature.iam.data.remote

import com.google.gson.annotations.SerializedName

/**
 * Remote Data Transfer Object representing the backend's sign-in response.
 *
 * The [role] field is included for completeness but is NOT used for authorization
 * decisions inside the app. Role is always extracted from the [token] JWT payload
 * via [JwtDecoder] to prevent transport-layer tampering.
 *
 * @property id       Server-assigned user identifier.
 * @property username The authenticated user's account name.
 * @property token    Signed JWT carrying role claims in its payload.
 * @property role     Raw role string echoed by the backend (informational only).
 */
data class SignInResource(
    @SerializedName("id")       val id: Int,
    @SerializedName("username") val username: String,
    @SerializedName("token")    val token: String,
    @SerializedName("role")     val role: String
)