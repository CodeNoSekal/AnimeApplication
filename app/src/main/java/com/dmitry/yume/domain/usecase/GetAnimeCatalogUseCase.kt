package com.dmitry.yume.domain.usecase

import androidx.paging.PagingData
import com.dmitry.yume.domain.models.Anime
import com.dmitry.yume.domain.repository.AnimeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAnimeCatalogUseCase @Inject constructor(
    private val repository: AnimeRepository
) {
    operator fun invoke(
        status: String,
        sort: String,
        order: String
    ): Flow<PagingData<Anime>> {
        return repository.getAnime(
            status = status,
            sort = sort,
            order = order
        )
    }
}