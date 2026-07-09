package com.dmitry.test.animeapplication.domain.repository

import com.dmitry.test.animeapplication.domain.models.User

interface ProfileRepository{
    suspend fun getUser(): ProfileResult
}

sealed interface ProfileResult {
    data class Success(
        val user: User
    ) : ProfileResult
    data class Error(
        val message: String?
    ) : ProfileResult
}

