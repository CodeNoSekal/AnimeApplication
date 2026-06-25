package com.dmitry.test.animeapplication.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmitry.test.animeapplication.domain.usecase.ObserveAuthStateUseCase
import com.dmitry.test.animeapplication.domain.usecase.ValidateSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RootViewModel @Inject constructor(
    observeAuthState: ObserveAuthStateUseCase,
    validateSession: ValidateSessionUseCase,

) : ViewModel() {

    init {
        viewModelScope.launch {
            validateSession()
        }
    }
    val authState: StateFlow<AuthGate> =
        observeAuthState()
            .map { logged -> if (logged) AuthGate.Authenticated else AuthGate.Unauthenticated }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AuthGate.Loading)
}

enum class AuthGate { Loading, Authenticated, Unauthenticated, Unverified}