package com.dmitry.yume.data.authorization

import kotlinx.coroutines.flow.Flow

interface TokenStorage {
    suspend fun saveTokens(
        accessToken: String,
        refreshToken: String)
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?

    suspend fun clear()
}