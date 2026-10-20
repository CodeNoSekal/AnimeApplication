package com.dmitry.yume.domain.usecase

import androidx.paging.PagingData
import com.dmitry.yume.domain.models.Anime
import com.dmitry.yume.domain.repository.ExploreRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetExplorationListUseCase @Inject constructor(
    private val repository: ExploreRepository
) {
    operator fun invoke(endpoint: String): Flow<PagingData<Anime>> {
        return repository.getCollection(endpoint)
    }
}