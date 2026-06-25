package com.dmitry.test.animeapplication.data.authorization

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TokenStorageImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : TokenStorage{
    override suspend fun saveAccessToken(token: String) {
        dataStore.edit { it[ACCESS_TOKEN] = token }
    }

    override suspend fun saveRefreshToken(token: String) {
        dataStore.edit { it[REFRESH_TOKEN] = token }
    }

    override suspend fun getAccessToken(): String? {
        return dataStore.data.first()[ACCESS_TOKEN]
    }

    override suspend fun getRefreshToken(): String? {
        return dataStore.data.first()[REFRESH_TOKEN]
    }

    override suspend fun clear() {
        dataStore.edit { it.clear() }
    }

    override val isLoggedIn: Flow<Boolean> =
        dataStore.data.map { preferences -> !preferences[ACCESS_TOKEN].isNullOrBlank() }

    companion object {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    }
}