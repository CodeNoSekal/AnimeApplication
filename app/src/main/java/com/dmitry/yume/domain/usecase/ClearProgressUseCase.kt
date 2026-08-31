package com.dmitry.yume.domain.usecase

import com.dmitry.yume.domain.repository.MeRepository
import com.dmitry.yume.domain.repository.OperationResult
import javax.inject.Inject

class ClearProgressUseCase @Inject constructor(
    private val repository: MeRepository
) {
    suspend operator fun invoke(animeId: Int): OperationResult = repository.clearProgress(animeId)
}
