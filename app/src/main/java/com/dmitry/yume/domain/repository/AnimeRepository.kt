package com.dmitry.yume.domain.repository

import androidx.paging.PagingData
import com.dmitry.yume.domain.models.Anime
import com.dmitry.yume.domain.models.AnimeDetailed
import com.dmitry.yume.domain.models.AppError
import com.dmitry.yume.domain.models.SearchOptions
import kotlinx.coroutines.flow.Flow

interface AnimeRepository {
    fun getAnime(
        options: SearchOptions = SearchOptions(),
    ): Flow<PagingData<Anime>>
    fun searchAnime(
        q: String,
        options: SearchOptions = SearchOptions()
    ): Flow<PagingData<Anime>>
    suspend fun getAnimeById(id: Int): AnimeDetailResult
    suspend fun getAnimeCount(options: SearchOptions = SearchOptions()): Int?
}



sealed interface AnimeDetailResult {
    data class Success(
        val anime: AnimeDetailed
    ) : AnimeDetailResult
    data class Error(
        val error: AppError
    ) : AnimeDetailResult
}