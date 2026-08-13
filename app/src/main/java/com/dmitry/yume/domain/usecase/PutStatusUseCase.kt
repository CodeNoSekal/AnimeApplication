package com.dmitry.yume.domain.usecase

import com.dmitry.yume.domain.repository.MeRepository
import com.dmitry.yume.domain.repository.StatusResult
import javax.inject.Inject

class PutStatusUseCase @Inject constructor(
    private val repository: MeRepository
) {
    suspend operator fun invoke(id: Int, status: String?): StatusResult{
        return repository.putStatus(id, status)
    }
}