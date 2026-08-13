package com.dmitry.yume.data.repository

import com.dmitry.yume.data.api.AuthApi
import com.dmitry.yume.data.api.VerificationApi
import com.dmitry.yume.data.authorization.AuthSessionManager
import com.dmitry.yume.data.authorization.StoredTokens
import com.dmitry.yume.data.authorization.TokenStorage
import com.dmitry.yume.data.request.LoginRequest
import com.dmitry.yume.data.request.RegisterRequest
import com.dmitry.yume.data.request.VerifyRequest
import com.dmitry.yume.data.response.AuthResponse
import com.dmitry.yume.data.response.UserDTO
import com.dmitry.yume.data.response.VerificationResponse
import com.dmitry.yume.domain.models.SessionState
import com.dmitry.yume.domain.models.User
import com.dmitry.yume.domain.repository.AuthErrorReason
import com.dmitry.yume.domain.repository.AuthResult
import com.dmitry.yume.domain.repository.OperationResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException

class AuthRepositoryImplTest {

    @Test
    fun `verify 422 is classified as incorrect code`() = runTest {
        val fixture = fixture(
            verificationApi = FakeVerificationApi(
                verify = { throw httpError(422) }
            )
        )
        fixture.sessionManager.establishSession(TOKENS, USER)

        val result = fixture.repository.verifyEmail("123456")

        assertTrue(result is AuthResult.Error)
        assertEquals(
            AuthErrorReason.IncorrectCode,
            (result as AuthResult.Error).reason
        )
    }

    @Test
    fun `resend exposes request failure`() = runTest {
        val fixture = fixture(
            verificationApi = FakeVerificationApi(
                send = { throw httpError(503) }
            )
        )

        assertTrue(fixture.repository.sendCode() is OperationResult.Error)
    }

    @Test(expected = CancellationException::class)
    fun `resend rethrows cancellation`() = runTest {
        val fixture = fixture(
            verificationApi = FakeVerificationApi(
                send = { throw CancellationException("cancelled") }
            )
        )

        fixture.repository.sendCode()
    }

    @Test
    fun `logout failure still clears local session`() = runTest {
        val fixture = fixture(
            authApi = FakeAuthApi(logout = { throw httpError(503) })
        )
        fixture.sessionManager.establishSession(TOKENS, USER)

        val result = fixture.repository.logout()

        assertTrue(result is OperationResult.Error)
        assertNull(fixture.storage.getTokens())
        assertEquals(
            SessionState.Unauthenticated,
            fixture.sessionManager.sessionState.value
        )
    }

    private fun fixture(
        authApi: AuthApi = FakeAuthApi(),
        verificationApi: VerificationApi = FakeVerificationApi()
    ): Fixture {
        val storage = FakeTokenStorage()
        val sessionManager = AuthSessionManager(storage)
        return Fixture(
            repository = AuthRepositoryImpl(authApi, verificationApi, sessionManager),
            sessionManager = sessionManager,
            storage = storage
        )
    }

    private fun httpError(code: Int) = HttpException(
        retrofit2.Response.error<Any>(code, "".toResponseBody())
    )

    private data class Fixture(
        val repository: AuthRepositoryImpl,
        val sessionManager: AuthSessionManager,
        val storage: FakeTokenStorage
    )

    private class FakeAuthApi(
        private val logout: suspend () -> Unit = {}
    ) : AuthApi {
        override suspend fun login(loginRequest: LoginRequest): AuthResponse = error("not used")
        override suspend fun register(registerRequest: RegisterRequest): AuthResponse = error("not used")
        override suspend fun logout() = logout.invoke()
        override suspend fun getMe(): UserDTO = error("not used")
    }

    private class FakeVerificationApi(
        private val send: suspend () -> Unit = {},
        private val verify: suspend (VerifyRequest) -> VerificationResponse = {
            VerificationResponse("ok")
        }
    ) : VerificationApi {
        override suspend fun sendCode() = send.invoke()
        override suspend fun verify(verifyRequest: VerifyRequest): VerificationResponse =
            verify.invoke(verifyRequest)
    }

    private class FakeTokenStorage : TokenStorage {
        private var tokens: StoredTokens? = null

        override suspend fun saveTokens(accessToken: String, refreshToken: String) {
            tokens = StoredTokens(accessToken, refreshToken)
        }

        override suspend fun getTokens(): StoredTokens? = tokens

        override suspend fun clear() {
            tokens = null
        }
    }

    private companion object {
        val TOKENS = StoredTokens("access", "refresh")
        val USER = User(1, "mail@example.com", false, "user", "User", null, false)
    }
}
