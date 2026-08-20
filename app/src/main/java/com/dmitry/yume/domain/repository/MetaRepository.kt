package com.dmitry.yume.domain.repository

import com.dmitry.yume.domain.models.Genres
import com.dmitry.yume.domain.models.Home

interface MetaRepository {
    suspend fun getHome() : HomeResult
    suspend fun getGenres() : GenresResult
}

sealed interface HomeResult {
    data class Success(
        val home: Home
    ) : HomeResult
    data class Error(
        val message: String?
    ) : HomeResult
}

sealed interface GenresResult {
    data class Success(
        val genres: Genres
    ) : GenresResult
    data class Error(
        val message: String?
    ) : GenresResult
}