// feature/iam/data/remote/IamApiService.kt
package com.smartstay.application_mobile_frontend.feature.iam.data.remote

import com.smartstay.application_mobile_frontend.feature.iam.domain.model.SignInCommand
import com.smartstay.application_mobile_frontend.feature.iam.domain.model.SignUpCommand
import retrofit2.http.Body
import retrofit2.http.POST

interface IamApiService {
    @POST("api/v1/authentication/sign-in")
    suspend fun signIn(@Body command: SignInCommand): SignInResource

    @POST("api/v1/authentication/sign-up")
    suspend fun signUp(@Body command: SignUpCommand): SignUpResource
}

// Tus "Resources" (DTOs)
data class SignInResource(val id: Int, val username: String, val token: String, val roles: List<String>?, val role: String?)
data class SignUpResource(val message: String)