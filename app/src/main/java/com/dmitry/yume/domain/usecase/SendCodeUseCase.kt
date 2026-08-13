package com.dmitry.yume.domain.usecase

import com.dmitry.yume.domain.repository.AuthRepository
import com.dmitry.yume.domain.repository.OperationResult
import javax.inject.Inject

class SendCodeUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): OperationResult = repository.sendCode()
}
