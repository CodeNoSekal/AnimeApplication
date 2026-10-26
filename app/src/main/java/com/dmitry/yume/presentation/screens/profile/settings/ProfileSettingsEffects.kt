package com.dmitry.yume.presentation.screens.profile.settings

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
internal fun ProfileSettingsEffects(
    state: ProfileSettingsViewState,
    snackbar: SnackbarHostState,
    viewModel: ProfileSettingsViewModel,
) {
    LaunchedEffect(state.error) {
        state.error?.let {
            snackbar.showSnackbar(it)
            viewModel.dismissError()
        }
    }
    LaunchedEffect(state.message) {
        state.message?.let {
            snackbar.showSnackbar(it)
            viewModel.consumeMessage()
        }
    }
}
