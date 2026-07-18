package com.dmitry.test.animeapplication.domain.usecase

import com.dmitry.test.animeapplication.domain.repository.MeRepository
import com.dmitry.test.animeapplication.domain.repository.ProgressResult
import javax.inject.Inject

class GetProgressUseCase @Inject constructor(
    private val repository: MeRepository
) {
    suspend operator fun invoke(): ProgressResult {
        return repository.getProgress()
    }
}