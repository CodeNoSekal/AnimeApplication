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
import kotlinx.coroutines.Job
import javax.inject.Inject

@HiltViewModel
class RootViewModel @Inject constructor(
    private val validateSession: ValidateSessionUseCase,
    observeSessionState: ObserveSessionStateUseCase
) : ViewModel() {
    private var validationJob: Job? = null

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
        if (validationJob?.isActive == true) return

        validationJob = viewModelScope.launch {
            validateSession()
        }
    }
}
