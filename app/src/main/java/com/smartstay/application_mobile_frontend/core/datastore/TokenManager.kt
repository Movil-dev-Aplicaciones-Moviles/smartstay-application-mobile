package com.smartstay.application_mobile_frontend.core.datastore

/**
 * Interfaz para la gestión de tokens JWT y datos del usuario autenticado.
 * La implementación concreta utiliza DataStore Preferences.
 */
interface TokenManager {
    suspend fun getToken(): String?
    suspend fun saveAuthData(token: String, userId: Int, username: String, role: String)
    suspend fun clearSession()
    suspend fun getUserId(): Int?
    suspend fun getUsername(): String?
    suspend fun getRole(): String?
}
