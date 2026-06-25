package com.dmitry.test.animeapplication.domain.repository

import androidx.paging.PagingData
import com.dmitry.test.animeapplication.domain.Anime
import kotlinx.coroutines.flow.Flow

interface AnimeRepository {
    fun getAnime(): Flow<PagingData<Anime>>
}