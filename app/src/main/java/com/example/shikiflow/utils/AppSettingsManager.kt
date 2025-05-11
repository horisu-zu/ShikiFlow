package com.example.shikiflow.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppSettingsManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("app_settings")
        private val THEME_KEY = stringPreferencesKey("theme")
        private val OLED_KEY = stringPreferencesKey("oled")
        private val LOCALE_KEY = stringPreferencesKey("locale")
    }

    val themeFlow: Flow<ThemeMode> = context.dataStore.data
        .map { preferences ->
            ThemeMode.fromString(preferences[THEME_KEY])
        }

    val oledFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[OLED_KEY]?.toBoolean() ?: false
        }

    val localeFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[LOCALE_KEY] ?: Locale.getDefault().language
        }

    suspend fun saveTheme(themeMode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = themeMode.name
        }
    }

    suspend fun saveOLEDMode(isEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[OLED_KEY] = isEnabled.toString()
        }
    }

    suspend fun saveLocale(locale: String) {
        context.dataStore.edit { preferences ->
            preferences[LOCALE_KEY] = locale
        }
    }
}