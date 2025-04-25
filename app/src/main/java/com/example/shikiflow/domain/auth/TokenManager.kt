package com.example.shikiflow.domain.auth

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.shikiflow.data.auth.TokenResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val scope: CoroutineScope
) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("auth")
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
    }

    /*private var cachedAccessToken: String? = null
    private var cachedRefreshToken: String? = null

    init {
        scope.launch {
            accessTokenFlow?.collect { token ->
                cachedAccessToken = token
            }
        }
        scope.launch {
            refreshTokenFlow?.collect { token ->
                cachedRefreshToken = token
            }
        }
    }

    fun getCachedAccessToken(): String? = cachedAccessToken
    fun getCachedRefreshToken(): String? = cachedRefreshToken*/

    val accessTokenFlow: Flow<String?> = context.dataStore.data
        .map { preferences ->
            Log.d("TokenManager", "AccessToken: ${preferences[ACCESS_TOKEN_KEY]}")
            preferences[ACCESS_TOKEN_KEY]
        }
        .catch { exception ->
            Log.e("TokenManager", "Error reading access token", exception)
            emit(null)
        }

    val refreshTokenFlow: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[REFRESH_TOKEN_KEY]
        }
        .catch { exception ->
            Log.e("TokenManager", "Error reading refresh token", exception)
            emit(null)
        }

    suspend fun saveTokens(tokenResponse: TokenResponse) {
        context.dataStore.edit { preferences ->
            tokenResponse.accessToken?.let {
                preferences[ACCESS_TOKEN_KEY] = it
                //cachedAccessToken = it
                Log.d("TokenManager", "Access token saved: $it")
            }
            tokenResponse.refreshToken?.let {
                preferences[REFRESH_TOKEN_KEY] = it
                //cachedRefreshToken = it
                Log.d("TokenManager", "Refresh token saved: $it")
            }
        }
    }

    suspend fun clearTokens() {
        context.dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
            preferences.remove(REFRESH_TOKEN_KEY)
        }
        //cachedAccessToken = null
        //cachedRefreshToken = null
    }

    //suspend fun getAccessToken(): String? = accessTokenFlow.firstOrNull()
    //suspend fun getRefreshToken(): String? = refreshTokenFlow.firstOrNull()
}