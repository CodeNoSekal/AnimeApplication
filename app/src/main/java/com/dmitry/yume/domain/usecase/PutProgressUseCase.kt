package com.dmitry.yume.domain.usecase

import com.dmitry.yume.domain.models.Progress
import com.dmitry.yume.domain.repository.MeRepository
import com.dmitry.yume.domain.repository.OperationResult
import javax.inject.Inject

class PutProgressUseCase @Inject constructor(
    private val repository: MeRepository
) {
    suspend operator fun invoke(progress: Progress): OperationResult =
        repository.putProgress(progress)
}
