package com.dmitry.yume.domain.usecase

import com.dmitry.yume.domain.repository.ExploreRepository
import com.dmitry.yume.domain.repository.ExploreResult
import javax.inject.Inject

class GetExploreDataUseCase @Inject constructor(
    private val repository: ExploreRepository
) {
    suspend operator fun invoke(): ExploreResult {
        return repository.getExplore()
    }
}