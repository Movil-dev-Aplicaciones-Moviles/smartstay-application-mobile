package com.smartstay.application_mobile_frontend.feature.iam.domain.repository

import com.smartstay.application_mobile_frontend.feature.iam.domain.model.SignInCommand
import com.smartstay.application_mobile_frontend.feature.iam.domain.model.SignUpCommand
import com.smartstay.application_mobile_frontend.feature.iam.domain.model.User

/**
 * Domain-driven repository interface for Identity and Access Management operations.
 *
 * Defines the contract between the domain layer and any data-layer implementation.
 * The interface is deliberately free of framework types (no LiveData, no Flow) so
 * the domain remains testable in isolation.
 *
 * The concrete implementation ([IamRepositoryImpl]) decides whether data comes from
 * the remote API, local SharedPreferences, or a combination of both.
 */
interface IamRepository {

    /**
     * Authenticates the user with the given credentials.
     *
     * On success the implementation is expected to persist the session locally
     * so that [isSignedIn] returns true on subsequent cold starts.
     *
     * @param command Credentials command carrying username and password.
     * @return The authenticated [User] domain entity with a resolved [UserRole].
     * @throws Exception (or a typed subclass) on network failure or invalid credentials.
     */
    suspend fun signIn(command: SignInCommand): User

    /**
     * Registers a new user account with the given credentials.
     *
     * Does NOT create a local session — the user must call [signIn] explicitly
     * after registration.
     *
     * @param command Registration command carrying the desired username and password.
     * @return true if the account was created successfully; false otherwise.
     * @throws Exception on network failure or backend validation errors.
     */
    suspend fun signUp(command: SignUpCommand): Boolean

    /**
     * Terminates the current session by clearing all locally persisted credentials.
     *
     * Navigation back to the sign-in screen is the caller's (ViewModel) responsibility.
     */
    fun signOut()

    /**
     * Returns true when a non-expired session token is present in local storage.
     *
     * Implementations should validate token expiry (via [JwtDecoder.isExpired]) so
     * that a stale session from a previous app run is not incorrectly treated as active.
     */
    fun isSignedIn(): Boolean

    /**
     * Returns the role string associated with the currently stored session.
     *
     * Returns "guest" when no session is active. Callers (typically the ViewModel)
     * are expected to convert this string to a [UserRole] via [UserRole.from].
     */
    fun getUserRole(): String
}