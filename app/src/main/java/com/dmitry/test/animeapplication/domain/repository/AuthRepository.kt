package com.dmitry.test.animeapplication.domain.repository

import kotlinx.coroutines.flow.Flow


sealed interface AuthResult {
    data object Success : AuthResult
    data object EmailNotVerified  : AuthResult
    data class Error(
        val message: String,
        val reason: AuthErrorReason = AuthErrorReason.Unknown
    ) : AuthResult
}

enum class AuthErrorReason {
    InvalidCredentials,
    EmailAlreadyUsed,
    IncorrectCode,
    Validation,
    Network,
    Server,
    Unknown,
}

interface AuthRepository {
    suspend fun register(email: String, password: String, displayName: String) : AuthResult
    suspend fun login(email: String, password: String) : AuthResult
    suspend fun sendCode()
    suspend fun verifyEmail(code: String): AuthResult
    suspend fun logout()
    suspend fun validateSession()
    val isLoggedIn: Flow<Boolean>
}