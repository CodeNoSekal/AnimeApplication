package com.dmitry.yume.domain.usecase

import com.dmitry.yume.domain.models.SessionState
import com.dmitry.yume.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSessionStateUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(): Flow<SessionState> =
        repository.sessionState
}