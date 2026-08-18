package com.dmitry.yume.data.authorization

import com.dmitry.yume.domain.models.SessionState
import com.dmitry.yume.domain.models.User
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

data class SessionSnapshot(
    val tokens: StoredTokens,
    val generation: Long
)

@Singleton
class AuthSessionManager @Inject constructor(
    private val tokenStorage: TokenStorage
) {
    private val mutex = Mutex()
    private val _sessionState = MutableStateFlow<SessionState>(SessionState.Loading)
    @Volatile
    private var tokenSnapshot: StoredTokens? = null
    private var generation = 0L

    val sessionState: StateFlow<SessionState> = _sessionState.asStateFlow()

    fun currentTokens(): StoredTokens? = tokenSnapshot

    suspend fun currentSession(): SessionSnapshot? = mutex.withLock {
        tokenSnapshot?.let { SessionSnapshot(it, generation) }
    }

    suspend fun beginValidation(): SessionSnapshot? = mutex.withLock {
        val previousTokens = tokenSnapshot
        tokenStorage.getTokens().also { tokens ->
            if (tokens != previousTokens) {
                generation++
            }
            tokenSnapshot = tokens
            _sessionState.value = if (tokens == null) {
                SessionState.Guest
            } else {
                SessionState.Loading
            }
        }?.let { SessionSnapshot(it, generation) }
    }

    suspend fun establishSession(tokens: StoredTokens, user: User?) = mutex.withLock {
        tokenStorage.saveTokens(tokens.accessToken, tokens.refreshToken)
        tokenSnapshot = tokens
        generation++
        _sessionState.value = if (user == null) {
            SessionState.Loading
        } else {
            SessionState.Authenticated(user)
        }
    }

    suspend fun replaceTokensIfCurrent(
        expectedRefreshToken: String,
        newTokens: StoredTokens
    ): Boolean = mutex.withLock {
        val current = tokenSnapshot
        if (current?.refreshToken != expectedRefreshToken) {
            false
        } else {
            tokenStorage.saveTokens(newTokens.accessToken, newTokens.refreshToken)
            tokenSnapshot = newTokens
            true
        }
    }

    suspend fun invalidateIfCurrent(expectedRefreshToken: String): Boolean = mutex.withLock {
        val current = tokenSnapshot
        if (current?.refreshToken != expectedRefreshToken) {
            false
        } else {
            tokenStorage.clear()
            tokenSnapshot = null
            generation++
            _sessionState.value = SessionState.Guest
            true
        }
    }

    suspend fun invalidateIfCurrent(expectedGeneration: Long): Boolean = mutex.withLock {
        if (generation != expectedGeneration) {
            false
        } else {
            tokenStorage.clear()
            tokenSnapshot = null
            generation++
            _sessionState.value = SessionState.Guest
            true
        }
    }

    suspend fun invalidate() = mutex.withLock {
        tokenStorage.clear()
        tokenSnapshot = null
        generation++
        _sessionState.value = SessionState.Guest
    }

    suspend fun setAuthenticatedIfCurrent(
        expectedGeneration: Long,
        user: User
    ): Boolean = mutex.withLock {
        if (generation != expectedGeneration || tokenSnapshot == null) {
            false
        } else {
            _sessionState.value = SessionState.Authenticated(user)
            true
        }
    }

    suspend fun setUnavailableIfCurrent(expectedGeneration: Long): Boolean = mutex.withLock {
        if (generation != expectedGeneration || tokenSnapshot == null) {
            false
        } else {
            _sessionState.value = SessionState.Unavailable
            true
        }
    }

    suspend fun updateAuthenticatedUser(
        expectedGeneration: Long,
        transform: (User) -> User
    ): Boolean = mutex.withLock {
        val state = _sessionState.value
        if (generation != expectedGeneration || state !is SessionState.Authenticated) {
            false
        } else {
            _sessionState.value = SessionState.Authenticated(transform(state.user))
            true
        }
    }
}
