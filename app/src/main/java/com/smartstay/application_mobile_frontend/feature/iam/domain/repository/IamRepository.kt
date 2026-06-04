// feature/iam/domain/repository/IamRepository.kt
package com.smartstay.application_mobile_frontend.feature.iam.domain.repository

import com.smartstay.application_mobile_frontend.feature.iam.domain.model.SignInCommand
import com.smartstay.application_mobile_frontend.feature.iam.domain.model.SignUpCommand
import com.smartstay.application_mobile_frontend.feature.iam.domain.model.User

/**
 * Domain-driven repository interface for Identity and Access Management operations.
 * Abstracts the underlying data sources (remote APIs, local caching).
 */
interface IamRepository {
    suspend fun signIn(command: SignInCommand): User
    suspend fun signUp(command: SignUpCommand): Boolean
    fun signOut()
    fun isSignedIn(): Boolean
    fun getUserRole(): String
}