package com.dmitry.yume.domain.repository

import com.dmitry.yume.domain.models.AppError
import com.dmitry.yume.domain.models.Meta
import com.dmitry.yume.domain.models.HomeData

interface MetaRepository {
    suspend fun getHome() : HomeResult
    suspend fun getGenres() : GenresResult
}

sealed interface HomeResult {
    data class Success(
        val homeData: HomeData
    ) : HomeResult
    data class Error(
        val error: AppError
    ) : HomeResult
}

sealed interface GenresResult {
    data class Success(
        val meta: Meta
    ) : GenresResult
    data class Error(
        val error: AppError
    ) : GenresResult
}