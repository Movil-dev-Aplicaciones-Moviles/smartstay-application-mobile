package com.smartstay.application_mobile_frontend.feature.iam.domain.model

/**
 * Domain entity representing an authenticated user within the SmartStay system.
 *
 * This is a pure domain object — it carries no framework annotations and has
 * no knowledge of persistence, networking, or UI. It is the single source of
 * truth for identity information flowing through the application after sign-in.
 *
 * @property id       Unique server-assigned identifier for this user.
 * @property username The user's account name as registered in the backend.
 * @property role     The resolved [UserRole] enum that governs dashboard routing
 *                    and feature access. Always a typed value — never a raw string.
 */
data class User(
    val id:       Int,
    val username: String,
    val role:     UserRole
)

/**
 * Command encapsulating the credentials required to authenticate an existing user.
 *
 * Follows the Command pattern (CQRS / DDD style) rather than a generic DTO so that
 * intent is explicit at the call site and validation can be added to the command itself.
 *
 * @property username The user's registered account name.
 * @property password The user's plain-text password (transported over HTTPS;
 *                    hashing is handled server-side).
 */
data class SignInCommand(
    val username: String,
    val password: String
)

/**
 * Command encapsulating the data required to register a new user account.
 *
 * No role is specified here — the backend assigns the default role on creation.
 * Role elevation is an administrative operation outside this bounded context.
 *
 * @property username The desired account name (subject to backend uniqueness constraints).
 * @property password The desired password in plain text (transported over HTTPS).
 */
data class SignUpCommand(
    val username: String,
    val password: String
)