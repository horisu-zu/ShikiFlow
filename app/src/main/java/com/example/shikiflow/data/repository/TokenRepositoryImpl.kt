package com.example.shikiflow.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.shikiflow.domain.model.auth.AuthCredentials
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.repository.TokenRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TokenRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
): TokenRepository {

    companion object {
        private fun accessTokenKey(authType: AuthType) =
            stringPreferencesKey("${authType.name.lowercase()}_access_token")

        private fun refreshTokenKey(authType: AuthType) =
            stringPreferencesKey("${authType.name.lowercase()}_refresh_token")
    }

    override fun authCredentials(authType: AuthType): Flow<AuthCredentials> = dataStore.data
        .map { preferences ->
            AuthCredentials(
                accessToken = preferences[accessTokenKey(authType)],
                refreshToken = preferences[refreshTokenKey(authType)]
            )
        }

    override suspend fun saveTokens(authCredentials: AuthCredentials, authType: AuthType) {
        dataStore.edit { preferences ->
            authCredentials.accessToken?.let { accessToken ->
                preferences[accessTokenKey(authType)] = accessToken
            }
            authCredentials.refreshToken?.let { refreshToken ->
                preferences[refreshTokenKey(authType)] = refreshToken
            }
        }
    }

    override suspend fun clearTokens() {
        dataStore.edit { preferences ->
            AuthType.entries.forEach { authType ->
                preferences.remove(accessTokenKey(authType))
                preferences.remove(refreshTokenKey(authType))
            }
        }
    }
}