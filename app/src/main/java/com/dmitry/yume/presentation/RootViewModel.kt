package com.dmitry.yume.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmitry.yume.domain.models.SessionState
import com.dmitry.yume.domain.usecase.ObserveSessionStateUseCase
import com.dmitry.yume.domain.usecase.ValidateSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RootViewModel @Inject constructor(
    private val validateSession: ValidateSessionUseCase,
    observeSessionState: ObserveSessionStateUseCase
) : ViewModel() {

    init {
        retrySessionValidation()
    }

    val sessionState: StateFlow<SessionState> =
        observeSessionState()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = SessionState.Loading
            )

    fun retrySessionValidation() {
        viewModelScope.launch {
            validateSession()
        }
    }
}