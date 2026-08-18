package com.dmitry.yume.domain.repository

import com.dmitry.yume.domain.models.Home

interface MetaRepository {
    suspend fun getHome() : HomeResult
}

sealed interface HomeResult {
    data class Success(
        val home: Home
    ) : HomeResult
    data class Error(
        val message: String?
    ) : HomeResult
}