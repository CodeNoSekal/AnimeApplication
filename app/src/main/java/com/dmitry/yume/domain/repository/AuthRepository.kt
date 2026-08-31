package com.dmitry.yume.domain.repository

import com.dmitry.yume.domain.models.SessionState
import kotlinx.coroutines.flow.Flow


sealed interface AuthResult {
    data object Success : AuthResult
    data object EmailNotVerified  : AuthResult
    data class Error(
        val message: String,
        val reason: AuthErrorReason = AuthErrorReason.Unknown
    ) : AuthResult
}

sealed interface SessionRefreshResult {
    data object Success : SessionRefreshResult
    data class Error(val message: String) : SessionRefreshResult
}

sealed interface ProfileDataResult<out T> {
    data class Success<T>(val data: T) : ProfileDataResult<T>
    data class Error(val message: String) : ProfileDataResult<Nothing>
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
    suspend fun sendCode(): OperationResult
    suspend fun verifyEmail(code: String): AuthResult
    suspend fun logout(): OperationResult
    suspend fun logoutAll(): OperationResult
    suspend fun validateSession()
    val sessionState: Flow<SessionState>
    suspend fun refreshCurrentUser(): SessionRefreshResult
    suspend fun updateProfile(username: String, displayName: String): OperationResult
    suspend fun checkUsername(username: String): ProfileDataResult<Boolean>
    suspend fun suggestUsername(displayName: String): ProfileDataResult<String>
    suspend fun changePassword(currentPassword: String, newPassword: String): OperationResult
    suspend fun changeEmail(newEmail: String, password: String): OperationResult
    suspend fun uploadAvatar(bytes: ByteArray, mimeType: String): OperationResult
    suspend fun removeAvatar(): OperationResult
}
