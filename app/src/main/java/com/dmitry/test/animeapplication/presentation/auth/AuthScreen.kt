package com.dmitry.test.animeapplication.presentation.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.dmitry.test.animeapplication.R
import com.dmitry.test.animeapplication.presentation.auth.components.YumeTextField

@Composable
fun AuthScreen(
    onAuthorized: () -> Unit,
    onCreated: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                AuthEvent.NavigateToHome -> onAuthorized()
                AuthEvent.NavigateToVerification -> onCreated()
            }
        }
    }

    val login = state.mode == AuthMode.Login

    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(360.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.34f),
                            Color.Transparent,
                        ),
                        center = Offset(x = Float.POSITIVE_INFINITY * 0.7f, y = 0f),
                        radius = 900f
                    )
                )
        )

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 26.dp)
                .padding(top = 64.dp, bottom = 28.dp)
        ) {
            Image(painterResource(R.drawable.ic_yume_logo), contentDescription = "Yume",
                modifier = Modifier.size(60.dp))
            Spacer(Modifier.height(30.dp))

            Text(if (login) "С возвращением" else "Добро пожаловать",
                style = MaterialTheme.typography.displaySmall)
            Spacer(Modifier.height(10.dp))
            Text(
                if (login) "Войдите, чтобы продолжить смотреть и вести списки."
                else "Создайте аккаунт Yume — тысячи тайтлов и ваши личные подборки.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.widthIn(max = 300.dp)
            )
            Spacer(Modifier.height(32.dp))

            if (!login) {
                YumeTextField(
                    value = state.username, onValueChange = viewModel::onUsernameChange,
                    placeholder = "Имя пользователя", leadingIcon = R.drawable.user_24,
                    isError = state.usernameError != null, errorText = state.usernameError
                )
                Spacer(Modifier.height(12.dp))
            }

            YumeTextField(
                value = state.email, onValueChange = viewModel::onEmailChange,
                placeholder = "Эл. почта", leadingIcon = R.drawable.envelope_24,
                keyboardType = KeyboardType.Email,
                isError = state.emailError != null, errorText = state.emailError,
            )
            Spacer(Modifier.height(12.dp))
            YumeTextField(
                value = state.password, onValueChange = viewModel::onPasswordChange,
                placeholder = "Пароль", leadingIcon = R.drawable.key_24,
                isPassword = true, passwordVisible = state.showPassword,
                onTogglePassword = viewModel::togglePasswordVisibility,
                isError = state.passwordError != null, errorText = state.passwordError,
            )

            if (login) {
                Spacer(Modifier.height(12.dp))
                Text("Забыли пароль?", color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.End).clickable { /* TODO экран сброса */})
            }

            state.formError?.let {
                Spacer(Modifier.height(12.dp))
                Text(it, color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(Modifier.height(20.dp))
            Button(
                onClick = viewModel::submit,
                enabled = state.submitEnabled,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(14.dp),
            ) {
                if (state.isLoading)
                    CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary)
                else
                    Text(if (login) "Войти" else "Создать аккаунт")
            }

            Spacer(Modifier.height(28.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 0.dp)
            ) {
                Text(
                    if (login) "Нет аккаунта? " else "Уже есть аккаунт? ",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    if (login) "Регистрация" else "Войти",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { viewModel.toggleMode() }
                )
            }

        }
    }
}




















