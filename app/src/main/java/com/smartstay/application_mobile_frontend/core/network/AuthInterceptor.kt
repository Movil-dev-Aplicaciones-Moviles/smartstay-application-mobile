// core/network/AuthInterceptor.kt
package com.smartstay.application_mobile_frontend.core.network

import com.smartstay.application_mobile_frontend.feature.iam.data.local.SessionManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val sessionManager: SessionManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = sessionManager.getToken()
        val originalRequest = chain.request()

        val requestBuilder = if (!token.isNullOrEmpty()) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
        } else {
            originalRequest.newBuilder()
        }

        return chain.proceed(requestBuilder.build())
    }
}