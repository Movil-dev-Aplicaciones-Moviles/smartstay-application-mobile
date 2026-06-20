package com.smartstay.application_mobile_frontend.feature.iam.data.mapper

import com.smartstay.application_mobile_frontend.feature.iam.data.dto.AuthenticatedUserResponse
import com.smartstay.application_mobile_frontend.feature.iam.data.dto.UserResponse
import com.smartstay.application_mobile_frontend.feature.iam.domain.model.AuthenticatedUser
import com.smartstay.application_mobile_frontend.feature.iam.domain.model.User

/**
 * Mapper de la capa de datos de IAM.
 *
 * Contiene funciones de extensión para convertir DTOs de red
 * a modelos de dominio.
 */

/**
 * Convierte un [AuthenticatedUserResponse] (DTO plano de red)
 * en un [AuthenticatedUser] (modelo de dominio).
 */
fun AuthenticatedUserResponse.toDomain(): AuthenticatedUser {
    return AuthenticatedUser(
        token = this.token,
        user = User(
            id = this.id,
            username = this.username,
            role = this.role,
            status = this.status ?: "Active",
            hotelId = this.hotelId,
            chainId = this.chainId,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    )
}

/**
 * Convierte un [UserResponse] (DTO de red)
 * en un [User] (modelo de dominio).
 */
fun UserResponse.toDomain(): User {
    return User(
        id = id,
        username = username,
        role = role,
        status = status ?: "Active",
        hotelId = hotelId,
        chainId = chainId,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

/**
 * Convierte una lista de [UserResponse] (DTO de red)
 * en una lista de [User] (modelos de dominio).
 */
fun List<UserResponse>.toDomain(): List<User> {
    return map { it.toDomain() }
}
