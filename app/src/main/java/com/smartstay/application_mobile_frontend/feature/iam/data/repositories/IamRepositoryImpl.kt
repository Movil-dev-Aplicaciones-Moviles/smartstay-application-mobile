package com.smartstay.application_mobile_frontend.feature.iam.data.repositories

import com.smartstay.application_mobile_frontend.feature.iam.data.local.SessionManager
import com.smartstay.application_mobile_frontend.feature.iam.data.mapper.toDomain
import com.smartstay.application_mobile_frontend.feature.iam.data.remote.IamApiService
import com.smartstay.application_mobile_frontend.feature.iam.data.remote.util.JwtDecoder
import com.smartstay.application_mobile_frontend.feature.iam.domain.model.SignInCommand
import com.smartstay.application_mobile_frontend.feature.iam.domain.model.SignUpCommand
import com.smartstay.application_mobile_frontend.feature.iam.domain.model.User
import com.smartstay.application_mobile_frontend.feature.iam.domain.repository.IamRepository
import javax.inject.Inject

/**
 * Concrete implementation of [IamRepository].
 *
 * Orchestrates the remote API call, local session persistence, and domain
 * model mapping. All business rules (role blocking, navigation) live in the
 * ViewModel layer — this class only moves and stores data.
 */
class IamRepositoryImpl @Inject constructor(
    private val api: IamApiService,
    private val sessionManager: SessionManager
) : IamRepository {

    /**
     * Authenticates the user against the backend, persists the session locally,
     * and returns the mapped [User] domain entity.
     *
     * Token and role are saved together so that a cold restart can restore both
     * without an extra network call.
     */
    override suspend fun signIn(command: SignInCommand): User {
        val resource     = api.signIn(command)
        val user         = resource.toDomain()

        // Persist token + resolved role string so SessionManager stays consistent.
        sessionManager.saveToken(
            token = resource.token,
            role  = user.role.value
        )

        return user
    }

    /**
     * Registers a new account. Returns true on any 2xx response.
     * No session is created at this point — the user must sign in separately.
     */
    override suspend fun signUp(command: SignUpCommand): Boolean {
        api.signUp(command)
        return true
    }

    /**
     * Clears the local session. The ViewModel is responsible for navigating
     * back to the sign-in screen after calling this.
     */
    override fun signOut() = sessionManager.clearSession()

    /**
     * Returns true only when a non-expired token is present in local storage.
     *
     * Consuming [JwtDecoder.isExpired] here prevents the app from treating a
     * stale session as active after a cold restart.
     */
    override fun isSignedIn(): Boolean {
        val token = sessionManager.getToken() ?: return false
        return !JwtDecoder.isExpired(token)
    }

    /**
     * Returns the role string stored during the last successful sign-in.
     * Delegates to [SessionManager.getRole] — defaults to "guest" when absent.
     */
    override fun getUserRole(): String = sessionManager.getRole()
}