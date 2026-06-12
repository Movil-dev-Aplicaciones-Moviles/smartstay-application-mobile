// core/network/AuthInterceptor.kt
package com.smartstay.application_mobile_frontend.core.network

import com.smartstay.application_mobile_frontend.feature.iam.data.local.SessionManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * OkHttp interceptor that attaches the stored JWT as a Bearer token
 * to every outgoing request.
 *
 * Requests made before sign-in (i.e. sign-in and sign-up themselves)
 * naturally have no token in [SessionManager], so the Authorization header
 * is simply omitted — the backend accepts those endpoints without it.
 */
@Singleton
class AuthInterceptor @Inject constructor(
    private val sessionManager: SessionManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val token    = sessionManager.getToken()

        val request = if (token != null) {
            original.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            original
        }

        return chain.proceed(request)
    }
}