package com.dmitry.test.animeapplication.domain.usecase

import com.dmitry.test.animeapplication.domain.repository.CurrentProgressResult
import com.dmitry.test.animeapplication.domain.repository.MeRepository
import javax.inject.Inject


class GetProgressByIdUseCase @Inject constructor(
    private val repository: MeRepository
) {
    suspend operator fun invoke(id: Int): CurrentProgressResult {
        return repository.getProgressById(id)
    }
}