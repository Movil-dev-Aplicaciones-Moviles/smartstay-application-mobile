// feature/iam/domain/model/IamModels.kt
package com.smartstay.application_mobile_frontend.feature.iam.domain.model

/**
 * Domain entity representing an authenticated user within the system.
 *
 * @property id Unique identifier for the user.
 * @property username The user's account name.
 * @property role The specific authorization role assigned to the user.
 */
data class User(
    val id: Int,
    val username: String,
    val role: UserRole
)

/**
 * Command to encapsulate the data required for a user sign-in operation.
 * * Follows the Command pattern for CQRS/DDD instead of generic data transfer objects.
 *
 * @property username The user's input username.
 * @property password The user's plain-text input password (to be securely processed).
 */
data class SignInCommand(
    val username: String,
    val password: String
)

/**
 * Command to encapsulate the data required to register a new user in the system.
 *
 * @property username The desired username.
 * @property password The desired password.
 */
data class SignUpCommand(
    val username: String,
    val password: String
)