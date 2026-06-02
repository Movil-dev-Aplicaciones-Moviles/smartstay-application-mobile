// feature/iam/data/remote/util/JwtDecoder.kt
package com.smartstay.application_mobile_frontend.feature.iam.data.remote.util

import android.util.Base64
import org.json.JSONObject
import java.io.UnsupportedEncodingException

object JwtDecoder {

    private const val DOTNET_ROLE_CLAIM = "http://schemas.microsoft.com/ws/2008/06/identity/claims/role"
    private const val SHORT_ROLE_CLAIM = "role"

    fun extractRoleFromJwt(token: String?): String {
        if (token.isNullOrBlank()) return "guest"

        try {
            val parts = token.split(".")
            if (parts.size < 2) return "guest"

            val payloadBase64 = parts[1]
            val decodedBytes = Base64.decode(payloadBase64, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
            val payloadString = String(decodedBytes, Charsets.UTF_8)

            val jsonObject = JSONObject(payloadString)

            return when {
                jsonObject.has(DOTNET_ROLE_CLAIM) -> jsonObject.getString(DOTNET_ROLE_CLAIM)
                jsonObject.has(SHORT_ROLE_CLAIM) -> jsonObject.getString(SHORT_ROLE_CLAIM)
                else -> "guest"
            }
        } catch (e: Exception) {
            return "guest"
        }
    }
}