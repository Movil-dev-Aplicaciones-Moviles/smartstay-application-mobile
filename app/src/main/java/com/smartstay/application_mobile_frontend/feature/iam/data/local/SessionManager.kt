// feature/iam/data/local/SessionManager.kt
package com.smartstay.application_mobile_frontend.feature.iam.data.local

import android.content.Context
import android.content.SharedPreferences
import com.smartstay.application_mobile_frontend.feature.iam.data.remote.util.JwtDecoder
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class SessionManager @Inject constructor(@ApplicationContext context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("smartstay_session", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        val role = JwtDecoder.extractRoleFromJwt(token)
        prefs.edit {
            putString("token", token)
                .putString("user_role", role)
        }
    }

    fun getToken(): String? = prefs.getString("token", null)

    fun getUserRole(): String = prefs.getString("user_role", "guest") ?: "guest"

    fun clearSession() = prefs.edit { clear() }
}