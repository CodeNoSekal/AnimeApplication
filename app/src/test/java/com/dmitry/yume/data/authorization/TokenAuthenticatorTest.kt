package com.dmitry.yume.data.authorization

import com.dmitry.yume.data.api.RefreshApi
import com.dmitry.yume.data.request.RefreshRequest
import com.dmitry.yume.data.response.AuthResponse
import com.dmitry.yume.domain.models.SessionState
import com.dmitry.yume.domain.models.User
import java.util.concurrent.atomic.AtomicInteger
import java.io.IOException
import javax.inject.Provider
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException

class TokenAuthenticatorTest {

    @Test
    fun `parallel unauthorized responses perform one refresh`() = runBlocking {
        val fixture = fixture(
            refresh = {
                delay(50)
                AuthResponse("new-access", "new-refresh", null)
            }
        )
        fixture.sessionManager.establishSession(OLD_TOKENS, USER)

        val retried = (1..8).map {
            async(Dispatchers.Default) {
                fixture.authenticator.authenticate(null, unauthorized("Bearer old-access"))
            }
        }.awaitAll()

        assertEquals(1, fixture.refreshCalls.get())
        assertTrue(retried.all { it?.header("Authorization") == "Bearer new-access" })
        assertEquals(StoredTokens("new-access", "new-refresh"), fixture.storage.getTokens())
        assertEquals(SessionState.Authenticated(USER), fixture.sessionManager.sessionState.value)
    }

    @Test
    fun `rejected refresh clears current session and notifies observers`() = runBlocking {
        val fixture = fixture(refresh = { throw unauthorizedRefresh() })
        fixture.sessionManager.establishSession(OLD_TOKENS, USER)

        val retried = fixture.authenticator.authenticate(
            null,
            unauthorized("Bearer old-access")
        )

        assertNull(retried)
        assertNull(fixture.storage.getTokens())
        assertEquals(SessionState.Guest, fixture.sessionManager.sessionState.value)
    }

    @Test
    fun `late rejected refresh cannot clear a newer login`() = runBlocking {
        val refreshStarted = CompletableDeferred<Unit>()
        val finishRefresh = CompletableDeferred<Unit>()
        val fixture = fixture(
            refresh = {
                refreshStarted.complete(Unit)
                finishRefresh.await()
                throw unauthorizedRefresh()
            }
        )
        fixture.sessionManager.establishSession(OLD_TOKENS, USER)

        val retry = async(Dispatchers.Default) {
            fixture.authenticator.authenticate(null, unauthorized("Bearer old-access"))
        }
        refreshStarted.await()
        fixture.sessionManager.establishSession(NEW_LOGIN_TOKENS, NEW_USER)
        finishRefresh.complete(Unit)

        assertEquals("Bearer login-access", retry.await()?.header("Authorization"))
        assertEquals(NEW_LOGIN_TOKENS, fixture.storage.getTokens())
        assertEquals(SessionState.Authenticated(NEW_USER), fixture.sessionManager.sessionState.value)
    }

    @Test
    fun `successful refresh rotates tokens without losing authenticated user`() = runBlocking {
        val fixture = fixture(
            refresh = { AuthResponse("new-access", "new-refresh", null) }
        )
        fixture.sessionManager.establishSession(OLD_TOKENS, USER)

        val retried = fixture.authenticator.authenticate(
            null,
            unauthorized("Bearer old-access")
        )

        assertEquals("Bearer new-access", retried?.header("Authorization"))
        assertEquals(StoredTokens("new-access", "new-refresh"), fixture.storage.getTokens())
        assertEquals(SessionState.Authenticated(USER), fixture.sessionManager.sessionState.value)
    }

    @Test
    fun `second unauthorized response with current token ends the session`() = runBlocking {
        val fixture = fixture(
            refresh = { error("Refresh must not be called for an already retried request") }
        )
        fixture.sessionManager.establishSession(OLD_TOKENS, USER)
        val first = unauthorized("Bearer old-access")
        val second = unauthorized("Bearer old-access", priorResponse = first)

        val retried = fixture.authenticator.authenticate(null, second)

        assertNull(retried)
        assertEquals(0, fixture.refreshCalls.get())
        assertNull(fixture.storage.getTokens())
        assertEquals(SessionState.Guest, fixture.sessionManager.sessionState.value)
    }

    @Test
    fun `late validation result cannot overwrite a newer login`() = runBlocking {
        val fixture = fixture(
            refresh = { error("Refresh is not used in this test") }
        )
        fixture.sessionManager.establishSession(OLD_TOKENS, USER)
        val oldSession = fixture.sessionManager.beginValidation()!!
        fixture.sessionManager.establishSession(NEW_LOGIN_TOKENS, NEW_USER)

        val accepted = fixture.sessionManager.setAuthenticatedIfCurrent(
            oldSession.generation,
            USER.copy(displayName = "Stale")
        )

        assertEquals(false, accepted)
        assertEquals(NEW_LOGIN_TOKENS, fixture.storage.getTokens())
        assertEquals(SessionState.Authenticated(NEW_USER), fixture.sessionManager.sessionState.value)
    }

    @Test
    fun `temporary refresh failure keeps the session for a later retry`() = runBlocking {
        val fixture = fixture(refresh = { throw IOException("offline") })
        fixture.sessionManager.establishSession(OLD_TOKENS, USER)

        val retried = fixture.authenticator.authenticate(
            null,
            unauthorized("Bearer old-access")
        )

        assertNull(retried)
        assertEquals(OLD_TOKENS, fixture.storage.getTokens())
        assertEquals(SessionState.Authenticated(USER), fixture.sessionManager.sessionState.value)
    }

    @Test
    fun `startup validation loads a persisted token pair into memory`() = runBlocking {
        val storage = FakeTokenStorage().apply {
            saveTokens(OLD_TOKENS.accessToken, OLD_TOKENS.refreshToken)
        }
        val sessionManager = AuthSessionManager(storage)

        val session = sessionManager.beginValidation()

        assertEquals(OLD_TOKENS, session?.tokens)
        assertEquals(OLD_TOKENS, sessionManager.currentTokens())
        assertEquals(SessionState.Loading, sessionManager.sessionState.value)
    }

    @Test
    fun `startup without a complete token pair is unauthenticated`() = runBlocking {
        val sessionManager = AuthSessionManager(FakeTokenStorage())

        val session = sessionManager.beginValidation()

        assertNull(session)
        assertNull(sessionManager.currentTokens())
        assertEquals(SessionState.Guest, sessionManager.sessionState.value)
    }

    @Test
    fun `late user update cannot modify a newer account`() = runBlocking {
        val fixture = fixture(
            refresh = { error("Refresh is not used in this test") }
        )
        fixture.sessionManager.establishSession(OLD_TOKENS, USER)
        val oldSession = fixture.sessionManager.currentSession()!!
        fixture.sessionManager.establishSession(NEW_LOGIN_TOKENS, NEW_USER)

        val accepted = fixture.sessionManager.updateAuthenticatedUser(oldSession.generation) {
            it.copy(emailVerified = true)
        }

        assertEquals(false, accepted)
        assertEquals(SessionState.Authenticated(NEW_USER), fixture.sessionManager.sessionState.value)
    }

    @Test
    fun `validation loaded from storage invalidates an older in memory generation`() = runBlocking {
        val storage = FakeTokenStorage()
        val sessionManager = AuthSessionManager(storage)
        sessionManager.establishSession(OLD_TOKENS, USER)
        val staleSession = sessionManager.currentSession()!!

        storage.saveTokens(NEW_LOGIN_TOKENS.accessToken, NEW_LOGIN_TOKENS.refreshToken)
        val loadedSession = sessionManager.beginValidation()!!

        assertTrue(loadedSession.generation > staleSession.generation)
        assertEquals(false, sessionManager.setAuthenticatedIfCurrent(staleSession.generation, USER))
        assertEquals(NEW_LOGIN_TOKENS, sessionManager.currentTokens())
    }

    private fun fixture(
        refresh: suspend (RefreshRequest) -> AuthResponse
    ): Fixture {
        val storage = FakeTokenStorage()
        val sessionManager = AuthSessionManager(storage)
        val refreshCalls = AtomicInteger()
        val refreshApi = object : RefreshApi {
            override suspend fun refresh(refreshRequest: RefreshRequest): AuthResponse {
                refreshCalls.incrementAndGet()
                return refresh(refreshRequest)
            }
        }
        return Fixture(
            storage = storage,
            sessionManager = sessionManager,
            authenticator = TokenAuthenticator(sessionManager, Provider { refreshApi }),
            refreshCalls = refreshCalls
        )
    }

    private fun unauthorized(
        authorization: String,
        priorResponse: Response? = null
    ): Response {
        val request = Request.Builder()
            .url("https://example.com/protected")
            .header("Authorization", authorization)
            .build()
        val builder = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(401)
            .message("Unauthorized")
            .body("".toResponseBody())

        if (priorResponse != null) {
            builder.priorResponse(
                Response.Builder()
                    .request(priorResponse.request)
                    .protocol(priorResponse.protocol)
                    .code(priorResponse.code)
                    .message(priorResponse.message)
                    .build()
            )
        }
        return builder.build()
    }

    private fun unauthorizedRefresh(): HttpException = HttpException(
        retrofit2.Response.error<AuthResponse>(401, "".toResponseBody())
    )

    private data class Fixture(
        val storage: FakeTokenStorage,
        val sessionManager: AuthSessionManager,
        val authenticator: TokenAuthenticator,
        val refreshCalls: AtomicInteger
    )

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
        val OLD_TOKENS = StoredTokens("old-access", "old-refresh")
        val NEW_LOGIN_TOKENS = StoredTokens("login-access", "login-refresh")
        val USER = User(1, "old@example.com", true, "old", "Old", null, false)
        val NEW_USER = User(2, "new@example.com", true, "new", "New", null, false)
    }
}
