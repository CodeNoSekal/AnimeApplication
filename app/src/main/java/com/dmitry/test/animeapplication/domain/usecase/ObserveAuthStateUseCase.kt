package com.dmitry.test.animeapplication.domain.usecase

import com.dmitry.test.animeapplication.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveAuthStateUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(): Flow<Boolean> = repository.isLoggedIn
}