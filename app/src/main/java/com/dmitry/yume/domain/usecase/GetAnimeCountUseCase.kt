package com.dmitry.yume.domain.usecase

import com.dmitry.yume.domain.models.SearchOptions
import com.dmitry.yume.domain.repository.AnimeRepository
import javax.inject.Inject

class GetAnimeCountUseCase @Inject constructor(
    private val repository: AnimeRepository
) {
    suspend operator fun invoke(options: SearchOptions = SearchOptions()): Int? {
        return repository.getAnimeCount(options)
    }
}
