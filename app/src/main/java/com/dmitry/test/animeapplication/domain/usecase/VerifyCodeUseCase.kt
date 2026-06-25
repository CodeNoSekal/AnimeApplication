package com.dmitry.test.animeapplication.domain.usecase

import com.dmitry.test.animeapplication.domain.repository.AuthRepository
import com.dmitry.test.animeapplication.domain.repository.AuthResult
import javax.inject.Inject

class VerifyCodeUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(code: String): AuthResult{
        return repository.verifyEmail(code)
    }
}