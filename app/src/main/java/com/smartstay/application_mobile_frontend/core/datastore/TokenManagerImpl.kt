package com.smartstay.application_mobile_frontend.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación concreta de [TokenManager] que almacena el token JWT
 * y los datos del usuario autenticado en DataStore Preferences.
 */
@Singleton
class TokenManagerImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : TokenManager {

    private companion object {
        val TOKEN = stringPreferencesKey("jwt_token")
        val USER_ID = intPreferencesKey("user_id")
        val USERNAME = stringPreferencesKey("username")
        val ROLE = stringPreferencesKey("role")
    }

    /**
     * Recupera el token JWT almacenado.
     * @return El token como String? o null si no existe.
     */
    override suspend fun getToken(): String? {
        return dataStore.data.map { preferences ->
            preferences[TOKEN]
        }.firstOrNull()
    }

    /**
     * Almacena el token JWT y los datos del usuario autenticado.
     * @param token Token JWT recibido del backend.
     * @param userId Identificador único del usuario.
     * @param username Nombre de usuario o correo electrónico.
     * @param role Rol del usuario (ej: GUEST, HOST, ADMIN).
     */
    override suspend fun saveAuthData(
        token: String,
        userId: Int,
        username: String,
        role: String
    ) {
        dataStore.edit { preferences ->
            preferences[TOKEN] = token
            preferences[USER_ID] = userId
            preferences[USERNAME] = username
            preferences[ROLE] = role
        }
    }

    /**
     * Elimina todos los datos de la sesión actual (cierre de sesión).
     */
    override suspend fun clearSession() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    /**
     * Recupera el identificador del usuario almacenado.
     * @return El userId como Int? o null si no hay sesión activa.
     */
    override suspend fun getUserId(): Int? {
        return dataStore.data.map { preferences ->
            preferences[USER_ID]
        }.firstOrNull()
    }

    /**
     * Recupera el nombre de usuario almacenado.
     * @return El username como String? o null si no hay sesión activa.
     */
    override suspend fun getUsername(): String? {
        return dataStore.data.map { preferences ->
            preferences[USERNAME]
        }.firstOrNull()
    }

    /**
     * Recupera el rol del usuario almacenado.
     * @return El role como String? o null si no hay sesión activa.
     */
    override suspend fun getRole(): String? {
        return dataStore.data.map { preferences ->
            preferences[ROLE]
        }.firstOrNull()
    }
}
