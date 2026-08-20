package com.dmitry.yume.domain.usecase

import com.dmitry.yume.domain.repository.GenresResult
import com.dmitry.yume.domain.repository.MetaRepository
import javax.inject.Inject

class GetGenresUseCase @Inject constructor(
    private val repository: MetaRepository
) {
    suspend operator fun invoke(): GenresResult = repository.getGenres()
}