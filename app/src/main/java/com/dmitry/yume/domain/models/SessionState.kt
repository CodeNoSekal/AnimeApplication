package com.dmitry.yume.domain.models

sealed interface SessionState {
    data object Loading : SessionState
    data object Unauthenticated : SessionState
    data class Authenticated(
        val user: User
    ) : SessionState

    data object Unavailable : SessionState
}