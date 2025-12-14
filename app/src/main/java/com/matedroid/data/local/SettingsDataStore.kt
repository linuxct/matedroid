package com.matedroid.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "matedroid_settings")

data class AppSettings(
    val serverUrl: String = "",
    val apiToken: String = "",
    val acceptInvalidCerts: Boolean = false
) {
    val isConfigured: Boolean
        get() = serverUrl.isNotBlank()
}

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val serverUrlKey = stringPreferencesKey("server_url")
    private val apiTokenKey = stringPreferencesKey("api_token")
    private val acceptInvalidCertsKey = booleanPreferencesKey("accept_invalid_certs")

    val settings: Flow<AppSettings> = context.dataStore.data.map { preferences ->
        AppSettings(
            serverUrl = preferences[serverUrlKey] ?: "",
            apiToken = preferences[apiTokenKey] ?: "",
            acceptInvalidCerts = preferences[acceptInvalidCertsKey] ?: false
        )
    }

    suspend fun saveSettings(serverUrl: String, apiToken: String, acceptInvalidCerts: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[serverUrlKey] = serverUrl
            preferences[apiTokenKey] = apiToken
            preferences[acceptInvalidCertsKey] = acceptInvalidCerts
        }
    }

    suspend fun clearSettings() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
