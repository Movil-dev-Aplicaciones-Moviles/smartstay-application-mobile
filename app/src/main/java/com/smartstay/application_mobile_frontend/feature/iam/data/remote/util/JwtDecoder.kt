// feature/iam/data/remote/util/JwtDecoder.kt
package com.smartstay.application_mobile_frontend.feature.iam.data.remote.util

import android.util.Base64
import org.json.JSONObject

/**
 * Utility object for inspecting and extracting claims from JSON Web Tokens.
 *
 * Decodes the JWT payload locally to avoid additional network round-trips
 * for role-based routing decisions. Does NOT verify the token signature —
 * signature validation is the backend's responsibility.
 *
 * Supports both the full .NET claim URI and the compact "role" shorthand
 * so the app remains compatible with future backend changes.
 */
object JwtDecoder {

    private const val DOTNET_ROLE_CLAIM =
        "http://schemas.microsoft.com/ws/2008/06/identity/claims/role"
    private const val SHORT_ROLE_CLAIM = "role"

    /**
     * Extracts the user role from the provided JWT string.
     *
     * Checks the full .NET claim URI first, then falls back to the compact key.
     *
     * @param token Raw JWT string (header.payload.signature).
     * @return The extracted role string, or "guest" as a safe default.
     */
    fun extractRoleFromJwt(token: String?): String {
        if (token.isNullOrBlank()) return ROLE_GUEST

        return try {
            val payload = decodePayload(token)
            when {
                payload.has(DOTNET_ROLE_CLAIM) -> payload.getString(DOTNET_ROLE_CLAIM)
                payload.has(SHORT_ROLE_CLAIM)  -> payload.getString(SHORT_ROLE_CLAIM)
                else                           -> ROLE_GUEST
            }
        } catch (_: Exception) {
            ROLE_GUEST
        }
    }

    /**
     * Checks whether the JWT's expiration claim ("exp") has already passed.
     *
     * @param token Raw JWT string.
     * @return true if the token is expired or cannot be parsed; false if still valid.
     */
    fun isExpired(token: String?): Boolean {
        if (token.isNullOrBlank()) return true

        return try {
            val payload = decodePayload(token)
            val exp     = payload.getLong("exp")
            val nowSecs = System.currentTimeMillis() / 1_000
            nowSecs >= exp
        } catch (_: Exception) {
            true
        }
    }

    // ── Private ──────────────────────────────────────────────────────────────

    private fun decodePayload(token: String): JSONObject {
        val encodedPayload = token.split(".")[1]
        val decodedBytes   = Base64.decode(
            encodedPayload,
            Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
        )
        return JSONObject(String(decodedBytes))
    }

    private const val ROLE_GUEST = "guest"
}