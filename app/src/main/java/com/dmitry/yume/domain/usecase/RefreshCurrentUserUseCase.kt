package com.dmitry.yume.domain.usecase

import com.dmitry.yume.domain.repository.AuthRepository
import javax.inject.Inject

class RefreshCurrentUserUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke() =
        repository.refreshCurrentUser()
}