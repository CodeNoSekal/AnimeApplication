package com.dmitry.yume.data.authorization

data class StoredTokens(
    val accessToken: String,
    val refreshToken: String
)

interface TokenStorage {
    suspend fun saveTokens(
        accessToken: String,
        refreshToken: String)
    suspend fun getTokens(): StoredTokens?
    suspend fun clear()
}
