package com.dmitry.yume.presentation.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dmitry.yume.R
import com.dmitry.yume.presentation.components.BaseButton
import com.dmitry.yume.presentation.screens.auth.components.YumeTextField
import com.dmitry.yume.presentation.screens.profile.components.Avatar
import com.dmitry.yume.presentation.ui.theme.YumeTheme
import com.dmitry.yume.presentation.ui.theme.YumeType

@Composable
fun EditProfileScreen(
    onBackClick: () -> Unit,
    viewModel: EditProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val user by viewModel.user.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    var selectedImage by remember { mutableStateOf<Uri?>(null) }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri ?: return@rememberLauncherForActivityResult
        selectedImage = uri
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbar.showSnackbar(it)
            viewModel.dismissError()
        }
    }
    LaunchedEffect(state.saved) {
        if (state.saved) onBackClick()
    }

    Scaffold(
        topBar = { ProfileTopBar("Редактирование профиля", onBackClick) },
        snackbarHost = { SnackbarHost(snackbar) },
        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.background,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Avatar(user?.avatarUrl)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(
                    onClick = { picker.launch("image/*") },
                    enabled = !state.isAvatarSaving,
                ) { Text("Выбрать фото") }
                TextButton(
                    onClick = viewModel::removeAvatar,
                    enabled = !state.isAvatarSaving,
                ) { Text("Удалить", color = YumeTheme.colors.danger) }
            }
            if (state.isAvatarSaving) CircularProgressIndicator()

            YumeTextField(
                value = state.displayName,
                onValueChange = viewModel::onDisplayNameChange,
                placeholder = "Отображаемое имя",
                leadingIcon = R.drawable.user_24,
                modifier = Modifier.fillMaxWidth(),
            )
            YumeTextField(
                value = state.username,
                onValueChange = viewModel::onUsernameChange,
                placeholder = "Логин",
                leadingIcon = R.drawable.user_24,
                modifier = Modifier.fillMaxWidth(),
                isError = state.usernameCheck == UsernameCheck.Taken,
                errorText = if (state.usernameCheck == UsernameCheck.Taken) "Логин уже занят" else null,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = when (state.usernameCheck) {
                        UsernameCheck.Checking -> "Проверяем логин…"
                        UsernameCheck.Available -> "Логин доступен"
                        UsernameCheck.Taken -> "Логин занят"
                        UsernameCheck.Idle -> "Латиница, цифры и подчёркивание"
                    },
                    style = YumeType.xs,
                    color = if (state.usernameCheck == UsernameCheck.Available)
                        YumeTheme.colors.success else YumeTheme.colors.textMuted,
                )
                TextButton(onClick = viewModel::suggestUsername) { Text("Предложить") }
            }

            BaseButton(
                onClick = viewModel::save,
                modifier = Modifier.fillMaxWidth(),
                text = if (state.isSaving) "Сохраняем…" else "Сохранить",
                disabled = state.isSaving || state.usernameCheck == UsernameCheck.Checking,
            )
        }
    }

    selectedImage?.let { uri ->
        AvatarCropDialog(
            uri = uri,
            onDismiss = { selectedImage = null },
            onCropped = { bytes ->
                selectedImage = null
                viewModel.uploadAvatar(bytes, "image/jpeg")
            },
            onError = { message ->
                selectedImage = null
                viewModel.reportError(message)
            },
        )
    }
}
