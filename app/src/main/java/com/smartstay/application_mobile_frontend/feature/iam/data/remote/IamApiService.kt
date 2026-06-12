package com.smartstay.application_mobile_frontend.feature.iam.data.remote

import com.smartstay.application_mobile_frontend.feature.iam.domain.model.SignInCommand
import com.smartstay.application_mobile_frontend.feature.iam.domain.model.SignUpCommand
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Retrofit interface declaring all IAM-related remote endpoints.
 *
 * Base URL is provided by the [NetworkModule] singleton.
 */
interface IamApiService {

    /**
     * Authenticates a user and returns a [SignInResource] containing the JWT.
     */
    @POST("api/v1/iam/sign-in")
    suspend fun signIn(@Body command: SignInCommand): SignInResource

    /**
     * Registers a new user account.
     * A 2xx response is sufficient to confirm success; the body is discarded.
     */
    @POST("api/v1/iam/sign-up")
    suspend fun signUp(@Body command: SignUpCommand)
}