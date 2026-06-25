package com.dmitry.test.animeapplication.data.authorization

import kotlinx.coroutines.flow.Flow

interface TokenStorage {
    suspend fun saveAccessToken(token: String)
    suspend fun saveRefreshToken(token: String)

    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?

    suspend fun clear()

    val isLoggedIn: Flow<Boolean>
}