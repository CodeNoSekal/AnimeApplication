package com.dmitry.yume.presentation.screens.profile.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dmitry.yume.R
import com.dmitry.yume.presentation.components.BaseButton
import com.dmitry.yume.presentation.screens.auth.components.YumeTextField

@Composable
fun ChangePasswordScreen(
    onBackClick: () -> Unit,
    viewModel: ProfileSettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    var current by rememberSaveable { mutableStateOf("") }
    var new by rememberSaveable { mutableStateOf("") }
    var confirmation by rememberSaveable { mutableStateOf("") }

    ProfileSettingsEffects(state, snackbar, viewModel)

    ProfileFormScaffold("Изменить пароль", onBackClick, snackbar) {
        PasswordField("Текущий пароль", current) { current = it }
        PasswordField("Новый пароль", new) { new = it }
        PasswordField("Повторите новый пароль", confirmation) { confirmation = it }
        BaseButton(
            onClick = { viewModel.changePassword(current, new, confirmation) },
            modifier = Modifier.fillMaxWidth(),
            text = if (state.isSaving) "Сохраняем…" else "Изменить пароль",
            disabled = state.isSaving,
        )
    }
}

@Composable
private fun PasswordField(label: String, value: String, onChange: (String) -> Unit) {
    var visible by rememberSaveable { mutableStateOf(false) }
    YumeTextField(
        value = value,
        onValueChange = onChange,
        placeholder = label,
        leadingIcon = R.drawable.key_24,
        isPassword = true,
        passwordVisible = visible,
        onTogglePassword = { visible = !visible },
    )
}
