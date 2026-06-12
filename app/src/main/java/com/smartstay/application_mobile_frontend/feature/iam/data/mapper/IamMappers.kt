package com.smartstay.application_mobile_frontend.feature.iam.data.mapper

import com.smartstay.application_mobile_frontend.feature.iam.data.remote.SignInResource
import com.smartstay.application_mobile_frontend.feature.iam.data.remote.util.JwtDecoder
import com.smartstay.application_mobile_frontend.feature.iam.domain.model.User
import com.smartstay.application_mobile_frontend.feature.iam.domain.model.UserRole

/**
 * Maps [SignInResource] (remote DTO) to the [User] domain entity.
 *
 * Role resolution priority:
 * 1. Extracted from the JWT payload (avoids trusting a raw string from the body).
 * 2. Falls back to [UserRole.GUEST] via [UserRole.from] if the claim is absent or unrecognized.
 *
 * The [SignInResource.role] field returned by the backend is intentionally ignored here;
 * the JWT is the authoritative source to prevent tampering at the transport layer.
 */
fun SignInResource.toDomain(): User {
    val rawRole      = JwtDecoder.extractRoleFromJwt(this.token)
    val resolvedRole = UserRole.from(rawRole)

    return User(
        id       = this.id,
        username = this.username,
        role     = resolvedRole
    )
}