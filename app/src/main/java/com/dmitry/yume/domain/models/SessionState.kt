package com.dmitry.yume.domain.models

sealed interface SessionState {
    data object Loading : SessionState
    data object Guest : SessionState
    data class Authenticated(
        val user: User
    ) : SessionState

    data object Unavailable : SessionState
}