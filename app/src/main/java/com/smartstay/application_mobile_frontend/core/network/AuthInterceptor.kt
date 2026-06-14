package com.smartstay.application_mobile_frontend.core.network

import android.util.Log
import com.smartstay.application_mobile_frontend.core.datastore.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody
import javax.inject.Inject

/**
 * Interceptor de OkHttp que añade el token JWT a todas las peticiones salientes,
 * excepto a las rutas de autenticación (sign-in / sign-up).
 *
 * Si el backend responde 401 habiendo enviado token, limpia automáticamente
 * la sesión en [TokenManager] para que la UI redirija al Login.
 */
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {

    companion object {
        private const val AUTH_HEADER = "Authorization"
        private const val BEARER_PREFIX = "Bearer "
        private const val SIGN_IN_PATH = "authentication/sign-in"
        private const val SIGN_UP_PATH = "authentication/sign-up"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestUrl = originalRequest.url.toString()

        // Excluir rutas de autenticación: no añadir token
        if (requestUrl.contains(SIGN_IN_PATH) || requestUrl.contains(SIGN_UP_PATH)) {
            return chain.proceed(originalRequest)
        }

        // Obtener el token de forma bloqueante (necesario para interceptores OkHttp)
        val token = runBlocking { tokenManager.getToken() }

        // Construir la request (con o sin token) y registrar si se añadió el header
        val tokenAdded = !token.isNullOrBlank()
        val newRequest = if (tokenAdded) {
            originalRequest.newBuilder()
                .addHeader(AUTH_HEADER, "$BEARER_PREFIX$token")
                .build()
        } else {
            originalRequest
        }

        val response = chain.proceed(newRequest)

        // Si se envió token y el backend responde 401, limpiar sesión automáticamente
        if (response.code == 401 && tokenAdded) {
            runBlocking { tokenManager.clearSession() }
            Log.w("AuthInterceptor", "Token expirado o inválido — sesión limpiada.")
            response.close()
            return response.newBuilder()
                .code(401)
                .message("Sesión expirada")
                .body(ResponseBody.create(null, ""))
                .build()
        }

        return response
    }
}
