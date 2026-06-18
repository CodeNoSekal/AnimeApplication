package com.dmitry.test.animeapplication.domain

import androidx.paging.PagingData
import com.dmitry.test.animeapplication.data.AnimeApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


interface AnimeRepository {
    fun getAnime(): Flow<PagingData<Anime>>
}


class GetAnimeCatalogUseCase @Inject constructor(
    private val repository: AnimeRepository
) {
    operator fun invoke(): Flow<PagingData<Anime>> {
        return repository.getAnime()
    }
}