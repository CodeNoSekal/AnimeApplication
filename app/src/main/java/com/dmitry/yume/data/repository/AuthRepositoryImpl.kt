package com.dmitry.yume.data.repository

import com.dmitry.yume.data.api.AuthApi
import com.dmitry.yume.data.api.VerificationApi
import com.dmitry.yume.data.authorization.AuthSessionManager
import com.dmitry.yume.data.authorization.StoredTokens
import com.dmitry.yume.data.request.LoginRequest
import com.dmitry.yume.data.request.ChangeEmailRequest
import com.dmitry.yume.data.request.ChangePasswordRequest
import com.dmitry.yume.data.request.ProfileRequest
import com.dmitry.yume.data.request.RegisterRequest
import com.dmitry.yume.data.request.VerifyRequest
import com.dmitry.yume.data.response.AuthResponse
import com.dmitry.yume.data.response.toDomain
import com.dmitry.yume.domain.models.SessionState
import com.dmitry.yume.domain.repository.AuthErrorReason
import com.dmitry.yume.domain.repository.AuthRepository
import com.dmitry.yume.domain.repository.AuthResult
import com.dmitry.yume.domain.repository.OperationResult
import com.dmitry.yume.domain.repository.ProfileDataResult
import com.dmitry.yume.domain.repository.SessionRefreshResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val verificationApi: VerificationApi,
    private val sessionManager: AuthSessionManager
) : AuthRepository {

    override val sessionState: Flow<SessionState> =
        sessionManager.sessionState

    override suspend fun refreshCurrentUser(): SessionRefreshResult {
        val session = sessionManager.currentSession()
            ?: return SessionRefreshResult.Error("Сессия истекла")

        try {
            val user = authApi.getMe().toDomain()
            sessionManager.setAuthenticatedIfCurrent(session.generation, user)
            return SessionRefreshResult.Success
        } catch (e: HttpException) {
            if (e.code() == 404) {
                sessionManager.invalidateIfCurrent(session.generation)
                return SessionRefreshResult.Error("Сессия истекла")
            } else if (e.code() == 401 && sessionManager.currentSession() == null) {
                return SessionRefreshResult.Error("Сессия истекла")
            } else {
                return SessionRefreshResult.Error("Не удалось обновить профиль")
            }
        } catch (e: IOException) {
            return SessionRefreshResult.Error("Нет подключения к интернету")
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            return SessionRefreshResult.Error("Не удалось обработать ответ сервера")
        }
    }

    override suspend fun register(
        email: String,
        password: String,
        displayName: String
    ): AuthResult =
        safeAuthCall {
            val response = authApi.register(RegisterRequest(email, password, displayName))
            persist(response)
            AuthResult.EmailNotVerified
        }

    override suspend fun login(email: String, password: String): AuthResult =
        safeAuthCall {
            val response = authApi.login(LoginRequest(email, password))
            persist(response)
            AuthResult.Success
        }

    override suspend fun sendCode(): OperationResult =
        try {
            verificationApi.sendCode()
            OperationResult.Success
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            OperationResult.Error(e.operationMessage("Не удалось отправить код"))
        }

    override suspend fun verifyEmail(code: String): AuthResult =
        safeAuthCall(validationReason = AuthErrorReason.IncorrectCode) {
            val session = sessionManager.currentSession()
                ?: return@safeAuthCall AuthResult.Error("Сессия истекла")

            verificationApi.verify(
                VerifyRequest(code)
            )

            val updated = sessionManager.updateAuthenticatedUser(session.generation) { user ->
                user.copy(emailVerified = true)
            }

            if (!updated) {
                validateSession()
            }

            AuthResult.Success
        }

    override suspend fun logout(): OperationResult {
        val result = try {
            authApi.logout()
            OperationResult.Success
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            OperationResult.Error(e.operationMessage("Не удалось завершить сессию на сервере"))
        } finally {
            withContext(NonCancellable) {
                sessionManager.invalidate()
            }
        }

        return result
    }

    override suspend fun logoutAll(): OperationResult {
        return try {
            authApi.logoutAll()
            withContext(NonCancellable) { sessionManager.invalidate() }
            OperationResult.Success
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            OperationResult.Error(e.operationMessage("Не удалось завершить все сеансы"))
        }
    }

    override suspend fun updateProfile(
        username: String,
        displayName: String
    ): OperationResult = profileMutation("Не удалось обновить профиль") { generation ->
        val user = authApi.updateProfile(
            ProfileRequest(username.trim(), displayName.trim())
        ).toDomain()
        sessionManager.setAuthenticatedIfCurrent(generation, user)
    }

    override suspend fun checkUsername(username: String): ProfileDataResult<Boolean> =
        try {
            ProfileDataResult.Success(authApi.usernameAvailable(username.trim()).available)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            ProfileDataResult.Error(e.operationMessage("Не удалось проверить логин"))
        }

    override suspend fun suggestUsername(displayName: String): ProfileDataResult<String> =
        try {
            ProfileDataResult.Success(authApi.suggestUsername(displayName.trim()).username)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            ProfileDataResult.Error(e.operationMessage("Не удалось предложить логин"))
        }

    override suspend fun changePassword(
        currentPassword: String,
        newPassword: String
    ): OperationResult = operation("Не удалось изменить пароль") {
        authApi.changePassword(ChangePasswordRequest(currentPassword, newPassword))
    }

    override suspend fun changeEmail(
        newEmail: String,
        password: String
    ): OperationResult = profileMutation("Не удалось изменить почту") { generation ->
        val response = authApi.changeEmail(ChangeEmailRequest(newEmail.trim(), password))
        sessionManager.updateAuthenticatedUser(generation) { user ->
            user.copy(email = response.email, emailVerified = response.emailVerified)
        }
    }

    override suspend fun uploadAvatar(bytes: ByteArray, mimeType: String): OperationResult =
        profileMutation("Не удалось загрузить аватар") { generation ->
            val body = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("file", "avatar", body)
            val response = authApi.uploadAvatar(part)
            sessionManager.updateAuthenticatedUser(generation) { user ->
                user.copy(avatarUrl = response.avatarUrl)
            }
        }

    override suspend fun removeAvatar(): OperationResult =
        profileMutation("Не удалось удалить аватар") { generation ->
            authApi.removeAvatar()
            val user = authApi.getMe().toDomain()
            sessionManager.setAuthenticatedIfCurrent(generation, user)
        }

    override suspend fun validateSession() {
        val session = sessionManager.beginValidation() ?: return

        try {
            val user = authApi.getMe().toDomain()
            sessionManager.setAuthenticatedIfCurrent(session.generation, user)
        } catch (e: HttpException) {
            if (e.code() == 404) {
                sessionManager.invalidateIfCurrent(session.generation)
            } else if (e.code() == 401 && sessionManager.currentSession() == null) {
                return
            } else {
                sessionManager.setUnavailableIfCurrent(session.generation)
            }
        } catch (_: IOException) {
            sessionManager.setUnavailableIfCurrent(session.generation)
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            sessionManager.setUnavailableIfCurrent(session.generation)
        }
    }

    private suspend fun persist(response: AuthResponse) {
        val user = response.user?.toDomain()
        sessionManager.establishSession(
            tokens = StoredTokens(response.accessToken, response.refreshToken),
            user = user
        )

        if (user == null) {
            validateSession()
        }
    }

    private suspend inline fun profileMutation(
        fallback: String,
        block: (generation: Long) -> Unit
    ): OperationResult {
        val session = sessionManager.currentSession()
            ?: return OperationResult.Error("Сессия истекла")
        return operation(fallback) { block(session.generation) }
    }

    private suspend inline fun operation(
        fallback: String,
        block: () -> Unit
    ): OperationResult = try {
        block()
        OperationResult.Success
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        OperationResult.Error(e.operationMessage(fallback))
    }

    private suspend inline fun safeAuthCall(
        validationReason: AuthErrorReason = AuthErrorReason.Validation,
        block: () -> AuthResult
    ): AuthResult =
        try {
            block()
        } catch (e: CancellationException) {
            throw e
        } catch (e: HttpException) {
            val reason = when(e.code()) {
                400, 422 -> validationReason
                401 -> AuthErrorReason.InvalidCredentials
                409 -> AuthErrorReason.EmailAlreadyUsed
                in 500..599 -> AuthErrorReason.Server
                else -> AuthErrorReason.Unknown
            }
            AuthResult.Error(message = mapMessage(reason, e), reason = reason)
        } catch (e: IOException) {
            AuthResult.Error("Нет подключения к интернету", AuthErrorReason.Network)
        } catch (_: Exception) {
            AuthResult.Error("Не удалось обработать ответ сервера", AuthErrorReason.Unknown)
        }

    private fun mapMessage(reason: AuthErrorReason, e: HttpException): String = when (reason) {
        AuthErrorReason.InvalidCredentials -> "Неверная почта или пароль"
        AuthErrorReason.EmailAlreadyUsed -> "Эта почта уже зарегистрирована"
        AuthErrorReason.IncorrectCode -> "Код неверный"
        AuthErrorReason.Validation -> parseServerDetail(e) ?: "Проверьте введённые данные"
        AuthErrorReason.Server -> "Сервер недоступен, попробуйте позже"
        else -> "Не удалось выполнить запрос"
    }

    private fun parseServerDetail(e: HttpException): String? =
        try {
            e.response()?.errorBody()?.string()?.takeIf { it.isNotBlank() }
                ?.let { JSONObject(it).optString("detail").ifBlank { null } }
        } catch (_: Exception) { null }

    private fun Exception.operationMessage(fallback: String): String =
        when (this) {
            is HttpException -> "$fallback (HTTP ${code()})"
            is IOException -> "Нет подключения к интернету"
            else -> message?.takeIf { it.isNotBlank() } ?: fallback
        }
}
