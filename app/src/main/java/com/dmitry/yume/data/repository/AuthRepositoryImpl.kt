package com.dmitry.yume.data.repository

import com.dmitry.yume.data.api.AuthApi
import com.dmitry.yume.data.api.VerificationApi
import com.dmitry.yume.data.authorization.TokenStorage
import com.dmitry.yume.data.request.LoginRequest
import com.dmitry.yume.data.request.RegisterRequest
import com.dmitry.yume.data.request.VerifyRequest
import com.dmitry.yume.data.response.AuthResponse
import com.dmitry.yume.data.response.toDomain
import com.dmitry.yume.domain.models.SessionState
import com.dmitry.yume.domain.repository.AuthErrorReason
import com.dmitry.yume.domain.repository.AuthRepository
import com.dmitry.yume.domain.repository.AuthResult
import com.dmitry.yume.domain.repository.SessionRefreshResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val verificationApi: VerificationApi,
    private val tokenStorage: TokenStorage
) : AuthRepository {

    private val _sessionState =
        MutableStateFlow<SessionState>(SessionState.Loading)

    override val sessionState: Flow<SessionState> =
        _sessionState.asStateFlow()

    override suspend fun refreshCurrentUser(): SessionRefreshResult {
        try {
            val user = authApi.getMe().toDomain()
            _sessionState.value = SessionState.Authenticated(user)
            return SessionRefreshResult.Success
        } catch (e: HttpException) {
            if (e.code() == 401 || e.code() == 404) {
                tokenStorage.clear()
                _sessionState.value =
                    SessionState.Unauthenticated

                return SessionRefreshResult.Error("Сессия истекла")
            } else {
                return SessionRefreshResult.Error("Не удалось обновить профиль")
            }
        } catch (e: IOException) {
            return SessionRefreshResult.Error("Нет подключения к интернету")
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

    override suspend fun sendCode() {
        try {
            verificationApi.sendCode()
        } catch (_: Exception){

        } finally {
        }
    }

    override suspend fun verifyEmail(code: String): AuthResult =
        safeAuthCall {
            verificationApi.verify(
                VerifyRequest(code)
            )

            val currentState = _sessionState.value

            if (currentState is SessionState.Authenticated) {
                _sessionState.value = currentState.copy(
                    user = currentState.user.copy(
                        emailVerified = true
                    )
                )
            } else {
                validateSession()
            }

            AuthResult.Success
        }

    override suspend fun logout() {
        try {
            authApi.logout()
        } catch (_: Exception){

        } finally {
            tokenStorage.clear()
            _sessionState.value = SessionState.Unauthenticated
        }
    }

    override suspend fun validateSession() {
        _sessionState.value = SessionState.Loading

        val accessToken = tokenStorage.getAccessToken()

        if (accessToken.isNullOrBlank()) {
            _sessionState.value = SessionState.Unauthenticated
            return
        }

        try {
            val user = authApi.getMe().toDomain()

            _sessionState.value =
                SessionState.Authenticated(user)
        } catch (e: HttpException) {
            if (e.code() == 401 || e.code() == 404) {
                tokenStorage.clear()
                _sessionState.value =
                    SessionState.Unauthenticated
            } else {
                _sessionState.value =
                    SessionState.Unavailable
            }
        } catch (_: IOException) {
            _sessionState.value =
                SessionState.Unavailable
        }
    }

    private suspend fun persist(response: AuthResponse) {
        tokenStorage.saveTokens(
            response.accessToken,
            response.refreshToken
        )

        val user = response.user?.toDomain()

        if (user != null){
            _sessionState.value = SessionState.Authenticated(user)
        } else {
            validateSession()
        }
    }

    private inline fun safeAuthCall(block: () -> AuthResult): AuthResult =
        try {
            block()
        } catch (e: HttpException) {
            val reason = when(e.code()) {
                400, 422 -> AuthErrorReason.Validation
                401 -> AuthErrorReason.InvalidCredentials
                409 -> AuthErrorReason.EmailAlreadyUsed
                in 500..599 -> AuthErrorReason.Server
                else -> AuthErrorReason.Unknown
            }
            AuthResult.Error(message = mapMessage(reason, e), reason = reason)
        } catch (e: IOException) {
            AuthResult.Error("Нет подключения к интернету", AuthErrorReason.Network)
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
}
