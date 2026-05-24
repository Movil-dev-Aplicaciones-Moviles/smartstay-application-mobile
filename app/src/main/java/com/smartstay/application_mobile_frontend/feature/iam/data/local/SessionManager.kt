// feature/iam/data/local/SessionManager.kt
package com.smartstay.application_mobile_frontend.feature.iam.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(@ApplicationContext context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("smartstay_session", Context.MODE_PRIVATE)

    fun saveToken(token: String) = prefs.edit().putString("token", token).apply()
    fun getToken(): String? = prefs.getString("token", null)
    fun clearSession() = prefs.edit().clear().apply()
}