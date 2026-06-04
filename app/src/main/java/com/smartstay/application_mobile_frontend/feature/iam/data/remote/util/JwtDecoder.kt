// feature/iam/data/remote/util/JwtDecoder.kt
package com.smartstay.application_mobile_frontend.feature.iam.data.remote.util

import android.util.Base64
import org.json.JSONObject

/**
 * Utility class for inspecting and extracting claims from JSON Web Tokens.
 * Avoids unnecessary backend calls for role-based routing decisions.
 */
object JwtDecoder {

    private const val DOTNET_ROLE_CLAIM = "http://schemas.microsoft.com/ws/2008/06/identity/claims/role"
    private const val SHORT_ROLE_CLAIM = "role"

    /**
     * Extracts the user role from the provided JWT token string.
     * * @return The extracted role or "guest" as a fallback.
     */
    fun extractRoleFromJwt(token: String?): String {
        if (token.isNullOrBlank()) return "guest"

        return try {
            val payload = decodePayload(token)
            when {
                payload.has(DOTNET_ROLE_CLAIM) -> payload.getString(DOTNET_ROLE_CLAIM)
                payload.has(SHORT_ROLE_CLAIM) -> payload.getString(SHORT_ROLE_CLAIM)
                else -> "guest"
            }
        } catch (_: Exception) {
            "guest"
        }
    }

    /**
     * Verifies if the temporal validity of the token has expired.
     */
    fun isExpired(token: String?): Boolean {
        if (token.isNullOrBlank()) return true

        return try {
            val payload = decodePayload(token)
            val exp = payload.getLong("exp")
            val now = System.currentTimeMillis() / 1000
            now >= exp
        } catch (_: Exception) {
            true
        }
    }

    private fun decodePayload(token: String): JSONObject {
        val payload = token.split(".")[1]
        val decoded = Base64.decode(
            payload,
            Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
        )
        return JSONObject(String(decoded))
    }
}