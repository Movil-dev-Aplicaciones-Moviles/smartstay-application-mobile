// feature/iam/data/repositories/IamRepositoryImpl.kt
package com.smartstay.application_mobile_frontend.feature.iam.data.repositories

import com.smartstay.application_mobile_frontend.feature.iam.data.local.SessionManager
import com.smartstay.application_mobile_frontend.feature.iam.data.mapper.toDomain
import com.smartstay.application_mobile_frontend.feature.iam.data.remote.IamApiService
import com.smartstay.application_mobile_frontend.feature.iam.domain.model.SignInCommand
import com.smartstay.application_mobile_frontend.feature.iam.domain.model.SignUpCommand
import com.smartstay.application_mobile_frontend.feature.iam.domain.model.User
import com.smartstay.application_mobile_frontend.feature.iam.domain.repository.IamRepository
import javax.inject.Inject

class IamRepositoryImpl @Inject constructor(
    private val api: IamApiService,
    private val sessionManager: SessionManager
) : IamRepository {

    override suspend fun signIn(command: SignInCommand): User {
        val resource = api.signIn(command)

        sessionManager.saveToken(resource.token)

        return resource.toDomain()
    }

    override suspend fun signUp(command: SignUpCommand): Boolean {
        api.signUp(command)
        return true
    }

    override fun signOut() = sessionManager.clearSession()

    override fun isSignedIn(): Boolean = sessionManager.getToken() != null
}