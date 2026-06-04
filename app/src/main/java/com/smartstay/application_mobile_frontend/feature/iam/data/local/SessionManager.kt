// feature/iam/data/local/SessionManager.kt
package com.smartstay.application_mobile_frontend.feature.iam.data.local

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Local data source responsible for persisting the user's authentication token and role claims.
 */
@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext context: Context
) {

    private val prefs = context.getSharedPreferences(
        "smartstay_session",
        Context.MODE_PRIVATE
    )

    /**
     * Commits the authentication metadata securely to local storage.
     */
    fun saveToken(token: String, role: String) {
        prefs.edit {
            putString("token", token)
            putString("role", role)
        }
    }

    fun getToken(): String? = prefs.getString("token", null)

    fun getRole(): String = prefs.getString("role", "guest") ?: "guest"

    /**
     * Purges all locally stored session data.
     */
    fun clearSession() {
        prefs.edit { clear() }
    }
}