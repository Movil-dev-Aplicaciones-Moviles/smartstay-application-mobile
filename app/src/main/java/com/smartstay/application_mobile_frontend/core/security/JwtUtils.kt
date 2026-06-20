package com.smartstay.application_mobile_frontend.core.security

import android.util.Base64
import org.json.JSONException
import org.json.JSONObject

/**
 * Utility for decoding the payload of a JWT without verifying the signature.
 *
 * A JWT consists of three Base64URL-encoded parts separated by dots:
 *   header.payload.signature
 *
 * This utility only decodes the second part (payload) to extract claims
 * such as the user's role and userId. It does NOT validate the token
 * signature — that responsibility belongs to the backend.
 *
 * All public functions return `null` on any error instead of throwing,
 * making them safe to call from ViewModels and Composables without
 * try/catch boilerplate.
 */
object JwtUtils {

    //region Public API

    /**
     * Decodes the payload section of a JWT and returns its claims as a [Map].
     *
     * @param token The raw JWT string (header.payload.signature).
     * @return A map of claim names to values, or `null` if decoding fails
     *         (malformed token, invalid Base64, or unparseable JSON).
     */
    fun decodePayload(token: String): Map<String, Any?>? {
        return try {
            val parts = token.split('.')
            if (parts.size < 2) return null

            val payload = parts[1]
            val decodedBytes = base64UrlSafeDecode(payload)
            val jsonString = String(decodedBytes, Charsets.UTF_8)
            val jsonObject = JSONObject(jsonString)

            jsonObjectToMap(jsonObject)
        } catch (_: IllegalArgumentException) {
            null
        } catch (_: JSONException) {
            null
        }
    }

    /**
     * Extracts the role claim from a JWT.
     *
     * Probes the following claim keys in order (the backend report states the
     * claim is named `Role`, but common alternatives are also checked):
     *   1. `"role"`
     *   2. `"Role"`
     *   3. `"http://schemas.microsoft.com/ws/2008/06/identity/claims/role"`
     *
     * @param token The raw JWT string.
     * @return The role string (e.g. `"Admin"`, `"Receptionist"`, `"Guest"`),
     *         or `null` if decoding fails or the claim is absent.
     */
    fun getRole(token: String): String? {
        val payload = decodePayload(token) ?: return null
        return payload["role"] as? String
            ?: payload["Role"] as? String
            ?: payload["http://schemas.microsoft.com/ws/2008/06/identity/claims/role"] as? String
    }

    /**
     * Extracts the userId claim (`sid`) from a JWT and returns it as an [Int].
     *
     * The backend report indicates the claim is named `Sid`. The value may be
     * represented as a JSON number or a numeric string; both are handled.
     *
     * @param token The raw JWT string.
     * @return The numeric user identifier, or `null` if decoding fails,
     *         the claim is absent, or the value cannot be parsed to an [Int].
     */
    fun getUserId(token: String): Int? {
        val payload = decodePayload(token) ?: return null
        val raw = payload["sid"] ?: payload["Sid"] ?: return null
        return when (raw) {
            is Int -> raw
            is String -> raw.toIntOrNull()
            else -> raw.toString().toIntOrNull()
        }
    }

    //endregion

    //region Private helpers

    /**
     * Decodes a Base64 URL-safe string, falling back to the default Base64
     * alphabet if URL-safe decoding is not supported by the API level.
     */
    private fun base64UrlSafeDecode(input: String): ByteArray {
        return try {
            Base64.decode(input, Base64.URL_SAFE)
        } catch (_: IllegalArgumentException) {
            Base64.decode(input, Base64.DEFAULT)
        }
    }

    /**
     * Recursively converts a [JSONObject] to a [Map]<[String], [Any?]>,
     * handling nested objects and preserving `null` values.
     */
    private fun jsonObjectToMap(jsonObject: JSONObject): Map<String, Any?> {
        val map = mutableMapOf<String, Any?>()
        for (key in jsonObject.keys()) {
            val value = jsonObject.get(key)
            map[key] = when (value) {
                is JSONObject -> jsonObjectToMap(value)
                JSONObject.NULL -> null
                else -> value
            }
        }
        return map
    }

    //endregion
}
