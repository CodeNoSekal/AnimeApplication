package com.dmitry.yume.domain.usecase

import com.dmitry.yume.domain.repository.MeRepository
import com.dmitry.yume.domain.repository.OperationResult
import javax.inject.Inject

class ClearAllProgressUseCase @Inject constructor(
    private val repository: MeRepository
) {
    suspend operator fun invoke(): OperationResult = repository.clearAllProgress()
}
