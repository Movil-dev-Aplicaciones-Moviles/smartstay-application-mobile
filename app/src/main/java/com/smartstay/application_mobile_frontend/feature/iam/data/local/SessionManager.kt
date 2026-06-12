package com.smartstay.application_mobile_frontend.feature.iam.data.local

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Local data source responsible for persisting the user's authentication token
 * and role claims across sessions via SharedPreferences.
 */
@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext context: Context
) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /**
     * Commits both the JWT token and the resolved role string to local storage.
     * Both values must be persisted together to guarantee consistency on restart.
     *
     * @param token Raw JWT string returned by the backend.
     * @param role  Normalized role string (e.g. "admin", "reception"). See [UserRole].
     */
    fun saveToken(token: String, role: String) {
        prefs.edit {
            putString(KEY_TOKEN, token)
            putString(KEY_ROLE, role)
        }
    }

    /**
     * Returns the stored JWT, or null if no session is active.
     */
    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    /**
     * Returns the stored role string. Defaults to "guest" when no session exists.
     */
    fun getRole(): String = prefs.getString(KEY_ROLE, ROLE_GUEST) ?: ROLE_GUEST

    /**
     * Purges all locally stored session data, effectively signing the user out.
     */
    fun clearSession() {
        prefs.edit { clear() }
    }

    companion object {
        private const val PREFS_NAME = "smartstay_session"
        private const val KEY_TOKEN  = "token"
        private const val KEY_ROLE   = "role"
        private const val ROLE_GUEST = "guest"
    }
}