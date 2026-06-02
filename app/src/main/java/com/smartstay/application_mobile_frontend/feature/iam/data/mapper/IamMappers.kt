// feature/iam/data/mapper/IamMappers.kt
package com.smartstay.application_mobile_frontend.feature.iam.data.mapper

import com.smartstay.application_mobile_frontend.feature.iam.data.remote.SignInResource
import com.smartstay.application_mobile_frontend.feature.iam.data.remote.util.JwtDecoder
import com.smartstay.application_mobile_frontend.feature.iam.domain.model.User

fun SignInResource.toDomain(): User {
    val extractedRole = JwtDecoder.extractRoleFromJwt(this.token)
    val normalizedRole = extractedRole.lowercase().trim()

    return User(
        id = this.id,
        username = this.username,
        role = normalizedRole,
        roles = listOf(normalizedRole)
    )
}