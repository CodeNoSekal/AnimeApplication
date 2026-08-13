package com.dmitry.yume.domain.usecase

import com.dmitry.yume.domain.models.Progress
import com.dmitry.yume.domain.repository.MeRepository
import javax.inject.Inject

class PutProgressUseCase @Inject constructor(
    private val repository: MeRepository
) {
    suspend operator fun invoke(progress: Progress) {
        repository.putProgress(progress)
    }
}