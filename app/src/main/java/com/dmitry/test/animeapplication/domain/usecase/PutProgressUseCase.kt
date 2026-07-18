package com.dmitry.test.animeapplication.domain.usecase

import com.dmitry.test.animeapplication.domain.models.Progress
import com.dmitry.test.animeapplication.domain.repository.MeRepository
import javax.inject.Inject

class PutProgressUseCase @Inject constructor(
    private val repository: MeRepository
) {
    suspend operator fun invoke(progress: Progress) {
        repository.putProgress(progress)
    }
}