package com.dmitry.test.animeapplication.data.repository

import com.dmitry.test.animeapplication.data.AuthApi
import com.dmitry.test.animeapplication.data.VerificationApi
import com.dmitry.test.animeapplication.data.authorization.TokenStorage
import com.dmitry.test.animeapplication.data.request.LoginRequest
import com.dmitry.test.animeapplication.data.request.RegisterRequest
import com.dmitry.test.animeapplication.data.request.VerifyRequest
import com.dmitry.test.animeapplication.data.response.AuthResponse
import com.dmitry.test.animeapplication.domain.repository.AuthErrorReason
import com.dmitry.test.animeapplication.domain.repository.AuthRepository
import com.dmitry.test.animeapplication.domain.repository.AuthResult
import kotlinx.coroutines.flow.Flow
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val verificationApi: VerificationApi,
    private val tokenStorage: TokenStorage
) : AuthRepository {

    override val isLoggedIn: Flow<Boolean> = tokenStorage.isLoggedIn

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
            verificationApi.verify(VerifyRequest(code))
            AuthResult.Success
        }

    override suspend fun logout() {
        try {
            authApi.logout()
        } catch (_: Exception){

        } finally {
            tokenStorage.clear()
        }
    }

    override suspend fun validateSession() {
        val token = tokenStorage.getAccessToken() ?: return
        try {
            authApi.getMe()
        } catch (e: HttpException) {
            if (e.code() in setOf(401, 403, 404)) {
                tokenStorage.clear()
            }
        } catch (_: IOException) {

        }
    }

    private suspend fun persist(response: AuthResponse) {
        tokenStorage.saveAccessToken(response.accessToken)
        tokenStorage.saveRefreshToken(response.refreshToken)
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
























