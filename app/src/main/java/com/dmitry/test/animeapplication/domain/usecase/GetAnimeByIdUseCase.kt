package com.dmitry.test.animeapplication.domain.usecase

import com.dmitry.test.animeapplication.domain.repository.AnimeDetailResult
import com.dmitry.test.animeapplication.domain.repository.AnimeRepository
import javax.inject.Inject

class GetAnimeByIdUseCase @Inject constructor(
    private val repository: AnimeRepository
) {
    suspend operator fun invoke(id: Int): AnimeDetailResult {
        return repository.getAnimeById(id)
    }
}