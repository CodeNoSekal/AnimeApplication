package com.dmitry.test.animeapplication.domain.usecase

import androidx.paging.PagingData
import com.dmitry.test.animeapplication.domain.Anime
import com.dmitry.test.animeapplication.domain.repository.AnimeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchAnimeUseCase @Inject constructor(
    private val repository: AnimeRepository
) {
    operator fun invoke(q: String): Flow<PagingData<Anime>> {
        return repository.searchAnime(q)
    }
}