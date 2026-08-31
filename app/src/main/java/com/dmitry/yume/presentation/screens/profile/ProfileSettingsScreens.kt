package com.dmitry.yume.presentation.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dmitry.yume.R
import com.dmitry.yume.presentation.components.BaseButton
import com.dmitry.yume.presentation.screens.auth.components.YumeTextField
import com.dmitry.yume.presentation.ui.theme.YumeTheme
import com.dmitry.yume.presentation.ui.theme.YumeType

@Composable
fun ProfileSettingsScreen(
    onBackClick: () -> Unit,
    onEmailClick: () -> Unit,
    onPasswordClick: () -> Unit,
    onLoggedOut: () -> Unit,
    viewModel: ProfileSettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    var confirmLogoutAll by rememberSaveable { mutableStateOf(false) }

    ProfileSettingsEffects(state, snackbar, viewModel)
    LaunchedEffect(state.loggedOut) {
        if (state.loggedOut) {
            viewModel.consumeMessage()
            onLoggedOut()
        }
    }

    Scaffold(
        topBar = { ProfileTopBar("Настройки аккаунта", onBackClick) },
        snackbarHost = { SnackbarHost(snackbar) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("Безопасность", style = YumeType.h2, color = YumeTheme.colors.textPrimary)
            SettingsRow("Изменить почту", R.drawable.envelope_24, onEmailClick)
            SettingsRow("Изменить пароль", R.drawable.key_24, onPasswordClick)
            SettingsRow(
                title = "Выйти на всех устройствах",
                icon = R.drawable.exit_24,
                onClick = { confirmLogoutAll = true },
                danger = true,
            )
        }
    }

    if (confirmLogoutAll) {
        AlertDialog(
            onDismissRequest = { confirmLogoutAll = false },
            title = { Text("Завершить все сеансы?") },
            text = { Text("Потребуется повторно войти на этом и остальных устройствах.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmLogoutAll = false
                    viewModel.logoutAll()
                }) { Text("Выйти везде", color = YumeTheme.colors.danger) }
            },
            dismissButton = {
                TextButton(onClick = { confirmLogoutAll = false }) { Text("Отмена") }
            },
        )
    }
}

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

@Composable
private fun ProfileFormScaffold(
    title: String,
    onBackClick: () -> Unit,
    snackbar: SnackbarHostState,
    content: @Composable ColumnScope.() -> Unit,
) {
    Scaffold(
        topBar = { ProfileTopBar(title, onBackClick) },
        snackbarHost = { SnackbarHost(snackbar) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            content = content,
        )
    }
}

@Composable
private fun SettingsRow(
    title: String,
    icon: Int,
    onClick: () -> Unit,
    danger: Boolean = false,
) {
    val contentColor = if (danger) YumeTheme.colors.danger else YumeTheme.colors.textPrimary
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = YumeTheme.colors.surfaceCard,
        border = BorderStroke(1.dp, YumeTheme.colors.lineStrong),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(painterResource(icon), contentDescription = null, tint = contentColor)
            Text(title, style = YumeType.bodyMedium, color = contentColor, modifier = Modifier.weight(1f))
            Icon(
                painterResource(R.drawable.angle_small_right),
                contentDescription = null,
                tint = YumeTheme.colors.textMuted,
            )
        }
    }
}

@Composable
private fun ProfileSettingsEffects(
    state: ProfileSettingsUiState,
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
