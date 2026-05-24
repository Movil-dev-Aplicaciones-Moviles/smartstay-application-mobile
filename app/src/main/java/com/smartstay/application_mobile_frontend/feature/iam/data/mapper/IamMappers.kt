// feature/iam/data/mapper/IamMappers.kt
package com.smartstay.application_mobile_frontend.feature.iam.data.mapper

import com.smartstay.application_mobile_frontend.feature.iam.data.remote.SignInResource
import com.smartstay.application_mobile_frontend.feature.iam.domain.model.User

fun SignInResource.toDomain(): User {
    val rawRole = this.roles?.firstOrNull() ?: this.role ?: "guest"

    return User(
        id = this.id,
        username = this.username,
        role = rawRole.lowercase().trim(),
        roles = this.roles ?: listOf(rawRole)
    )
}