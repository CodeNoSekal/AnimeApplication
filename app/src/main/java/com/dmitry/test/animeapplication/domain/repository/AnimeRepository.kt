package com.dmitry.test.animeapplication.domain.repository

import androidx.paging.PagingData
import com.dmitry.test.animeapplication.domain.models.Anime
import com.dmitry.test.animeapplication.domain.models.AnimeDetailed
import kotlinx.coroutines.flow.Flow

interface AnimeRepository {
    fun getAnime(): Flow<PagingData<Anime>>
    fun searchAnime(q: String): Flow<PagingData<Anime>>
    suspend fun getAnimeById(id: Int): AnimeDetailResult
}



sealed interface AnimeDetailResult {
    data class Success(
        val anime: AnimeDetailed
    ) : AnimeDetailResult
    data class Error(
        val message: String?
    ) : AnimeDetailResult
}