package com.dmitry.test.animeapplication.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmitry.test.animeapplication.domain.repository.ProfileResult
import com.dmitry.test.animeapplication.domain.usecase.GetUserUseCase
import com.dmitry.test.animeapplication.domain.usecase.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUser: GetUserUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = getUser()) {
                is ProfileResult.Success ->
                    _state.update { it.copy(isLoading = false, user = result.user) }
                is ProfileResult.Error ->
                    _state.update { it.copy(isLoading = false, error = result.message) }
            }
        }
    }


    fun logout() = viewModelScope.launch { logoutUseCase() }
}