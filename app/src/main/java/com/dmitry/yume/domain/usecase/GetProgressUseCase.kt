package com.dmitry.yume.domain.usecase

import com.dmitry.yume.domain.repository.MeRepository
import com.dmitry.yume.domain.repository.ProgressResult
import javax.inject.Inject

class GetProgressUseCase @Inject constructor(
    private val repository: MeRepository
) {
    suspend operator fun invoke(): ProgressResult {
        return repository.getProgress()
    }
}