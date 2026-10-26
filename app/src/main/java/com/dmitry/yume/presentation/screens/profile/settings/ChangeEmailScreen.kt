package com.dmitry.yume.presentation.screens.profile.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dmitry.yume.R
import com.dmitry.yume.presentation.components.BaseButton
import com.dmitry.yume.presentation.screens.auth.components.YumeTextField
import com.dmitry.yume.presentation.ui.theme.YumeTheme
import com.dmitry.yume.presentation.ui.theme.YumeType

@Composable
fun ChangeEmailScreen(
    onBackClick: () -> Unit,
    onVerificationRequired: () -> Unit,
    viewModel: ProfileSettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    ProfileSettingsEffects(state, snackbar, viewModel)
    LaunchedEffect(state.emailChanged) {
        if (state.emailChanged) {
            viewModel.consumeMessage()
            onVerificationRequired()
        }
    }

    ProfileFormScaffold("Изменить почту", onBackClick, snackbar) {
        Text(
            "На новый адрес придёт код подтверждения.",
            style = YumeType.sm,
            color = YumeTheme.colors.textMuted,
        )
        YumeTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = "Новая почта",
            leadingIcon = R.drawable.envelope_24,
            keyboardType = KeyboardType.Email,
        )
        YumeTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = "Текущий пароль",
            leadingIcon = R.drawable.key_24,
            isPassword = true,
            passwordVisible = passwordVisible,
            onTogglePassword = { passwordVisible = !passwordVisible },
        )
        BaseButton(
            onClick = { viewModel.changeEmail(email, password) },
            modifier = Modifier.fillMaxWidth(),
            text = if (state.isSaving) "Сохраняем…" else "Изменить почту",
            disabled = state.isSaving,
        )
    }
}
