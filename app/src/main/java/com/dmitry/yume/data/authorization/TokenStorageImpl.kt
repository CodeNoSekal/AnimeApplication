package com.dmitry.yume.data.authorization

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class TokenStorageImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : TokenStorage{
    override suspend fun saveTokens(
        accessToken: String,
        refreshToken: String
    ) {
        dataStore.edit { preferences ->
            val key = preferences[CIPHER_KEY] ?: TokenCipher.generateKeyHex().also {
                preferences[CIPHER_KEY] = it
            }
            preferences[ACCESS_TOKEN] = TokenCipher.encrypt(accessToken, key)
            preferences[REFRESH_TOKEN] = TokenCipher.encrypt(refreshToken, key)
        }
    }

    override suspend fun getTokens(): StoredTokens? {
        val preferences = dataStore.data.first()
        val key = preferences[CIPHER_KEY]
        val encryptedAccess = preferences[ACCESS_TOKEN]
        val encryptedRefresh = preferences[REFRESH_TOKEN]

        if (key.isNullOrBlank() || encryptedAccess.isNullOrBlank() || encryptedRefresh.isNullOrBlank()) {
            return null
        }

        return try {
            StoredTokens(
                accessToken = TokenCipher.decrypt(encryptedAccess, key),
                refreshToken = TokenCipher.decrypt(encryptedRefresh, key)
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // Повреждённый или несовместимый шифротекст (например, после
            // частичного восстановления бэкапа) — считаем сессию невалидной.
            clear()
            null
        }
    }

    override suspend fun clear() {
        dataStore.edit {
            it.remove(ACCESS_TOKEN)
            it.remove(REFRESH_TOKEN)
            it.remove(CIPHER_KEY)
        }
    }

    companion object {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val CIPHER_KEY = stringPreferencesKey("token_cipher_key")
    }
}
