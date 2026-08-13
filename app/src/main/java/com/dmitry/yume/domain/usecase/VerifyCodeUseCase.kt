package com.dmitry.yume.domain.usecase

import com.dmitry.yume.domain.repository.AuthRepository
import com.dmitry.yume.domain.repository.AuthResult
import javax.inject.Inject

class VerifyCodeUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(code: String): AuthResult{
        return repository.verifyEmail(code)
    }
}