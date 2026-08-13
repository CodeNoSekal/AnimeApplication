package com.dmitry.yume.domain.usecase

import com.dmitry.yume.domain.repository.MeRepository
import com.dmitry.yume.domain.repository.StatusResult
import javax.inject.Inject


class PutFavoriteUseCase @Inject constructor(
    private val repository: MeRepository
) {
    suspend operator fun invoke(id: Int, favorite: Boolean): StatusResult {
        return repository.putFavorite(id, favorite)
    }
}