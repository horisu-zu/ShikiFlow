package com.example.shikiflow.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.shikiflow.domain.model.auth.TokenResponse
import com.example.shikiflow.domain.repository.TokenRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TokenRepositoryImpl @Inject constructor(
    private val context: Context
): TokenRepository {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("auth")
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
    }

    override val tokensFlow: Flow<TokenResponse> = context.dataStore.data
        .map { preferences ->
            TokenResponse(
                accessToken = preferences[ACCESS_TOKEN_KEY],
                refreshToken = preferences[REFRESH_TOKEN_KEY]
            )
        }

    override suspend fun saveTokens(tokenResponse: TokenResponse) {
        context.dataStore.edit { preferences ->
            tokenResponse.accessToken?.let { accessToken ->
                preferences[ACCESS_TOKEN_KEY] = accessToken
            }
            tokenResponse.refreshToken?.let { refreshToken ->
                preferences[REFRESH_TOKEN_KEY] = refreshToken
            }
        }
    }

    override suspend fun clearTokens() {
        context.dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
            preferences.remove(REFRESH_TOKEN_KEY)
        }
    }
}