package com.dmitry.yume.domain.usecase

import com.dmitry.yume.domain.repository.AnimeDetailResult
import com.dmitry.yume.domain.repository.AnimeRepository
import javax.inject.Inject

class GetAnimeByIdUseCase @Inject constructor(
    private val repository: AnimeRepository
) {
    suspend operator fun invoke(id: Int): AnimeDetailResult {
        return repository.getAnimeById(id)
    }
}