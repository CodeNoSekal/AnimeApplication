package com.dmitry.yume.domain.usecase

import androidx.paging.PagingData
import com.dmitry.yume.domain.models.Anime
import com.dmitry.yume.domain.repository.MeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAnimeListByStatusUseCase @Inject constructor(
    private val repository: MeRepository
) {
    operator fun invoke(staus: String, q: String?): Flow<PagingData<Anime>> {
        return repository.getAnimeListByStatus(staus, q)
    }
}