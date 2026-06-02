// feature/iam/data/remote/IamApiService.kt
package com.smartstay.application_mobile_frontend.feature.iam.data.remote

import com.smartstay.application_mobile_frontend.feature.iam.domain.model.SignInCommand
import com.smartstay.application_mobile_frontend.feature.iam.domain.model.SignUpCommand
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface IamApiService {
    @POST("api/v1/authentication/sign-in")
    suspend fun signIn(@Body command: SignInCommand): SignInResource

    @POST("api/v1/authentication/sign-up")
    suspend fun signUp(@Body command: SignUpCommand): Response<Unit>
}

data class SignInResource(
    val id: Int,
    val username: String,
    val token: String
)